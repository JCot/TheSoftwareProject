import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;


public class TeamLead extends Worker {
	private String team;
	private String devNumber;
	private boolean isBusy;
	private Manager manager;
	private LinkedList<Employee> questionQueue = new LinkedList<Employee>();
	private ArrayList<Integer> questionTimes;
	private boolean questionAnswered;
	
	public TeamLead(String name, String devNumber, String teamNumber, Clock clock, CountDownLatch start, MeetingController meetings, Manager manager){
		super(name, clock, start, meetings);
		this.team = teamNumber;
		this.devNumber = devNumber;
		this.arrivalTime = rand.nextInt(30);
		this.lunchEndTime = rand.nextInt(480 - 240) + 240;
		this.timeAtLunch = rand.nextInt(30 - this.arrivalTime) + 30;
		this.manager = manager;
		
		this.questionTimes = new ArrayList<Integer>();
		
		int numQuestions = 1;//rand.nextInt((Main.maxLeadQues - Main.minLeadQues) + 1) + Main.minLeadQues;
		
		for(int i = 0; i < numQuestions; i++){
			questionTimes.add(rand.nextInt(480 - 90) + 90);
		}
		
		Collections.sort(questionTimes);
	}
	
	public synchronized boolean isBusy(){
		return isBusy;
	}
	
	public synchronized void getInLine(Employee e){
		questionQueue.add(e);
	}
	
	public synchronized void setQuestionAnswered(){
		this.questionAnswered = true;
	}
	
	//Try and answer a team members question
	public void answerQuestion(Employee developer){
		boolean canAnswer = (rand.nextInt(2) == 1);
		synchronized (this) {
			isBusy = true;
		}
		//If it is greater than 4pm then any remaining questions must wait until tomorrow
		if(clock.getClock() >= 480){
			System.out.println(clock.getFormattedClock() + "  " + name + " requests that the question asked by " + developer.name + " is held off until the next work day.");
			developer.setQuestionAnswered();
			synchronized (this) {
				isBusy = false;
				this.notifyAll();
			}
			return;
		}
		
		if(canAnswer){
			System.out.println(clock.getFormattedClock() + "  " + name + " answers a question for " + developer.name +".");
			developer.setQuestionAnswered();
			synchronized (this) {
				isBusy = false;
				this.notifyAll();
			}
			return;
		} else {
			System.out.println(clock.getFormattedClock() + "  " + name + " attempts to answer " + developer.name  +"'s question but can't answer it.");
			questionAnswered = false;
			this.askQuestion();
			developer.setQuestionAnswered();
		}
		
	}
	
	@Override
	public void askQuestion(){
		synchronized (this) {
			isBusy = true;
		}
		if(manager.isBusy()){
			System.out.println(clock.getFormattedClock() + "  " + name + " waits in line to ask the manager a question.");
		} else {
			System.out.println(clock.getFormattedClock() + "  " + name + " asks the manager a question.");
		}
		manager.getInLine(this);
		synchronized(manager) {
			try {
				while(this.questionAnswered == false){
					manager.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		synchronized (this) {
			isBusy = false;
			this.notifyAll();
		}

	}
	
	//Go to morning meeting with PM
	public void goToManagerMeeting(){
		synchronized (this) {
			isBusy = true;
		}
		CountDownLatch managerStandup = this.meetings.getManagerMeeting();
		System.out.println(clock.getFormattedClock() + "  " + name + " knocks on the manager's door.");
		
		managerStandup.countDown();
		
		int timeBeforeWaiting = clock.getClock();
		try{
			managerStandup.await();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		int timeAfterWaiting = clock.getClock();
		this.timeWorked += (timeAfterWaiting - timeBeforeWaiting);
		
		this.timeLapseWorking(15);
		
		synchronized (this) {
			isBusy = false;
		}
		timeInMeetings += 15;
	}
	
	@Override
	public void goToTeamStandUpMeeting(){
		System.out.println(clock.getFormattedClock() + "  " + name + " waits for team members to arrive to work.");
		CyclicBarrier teamStandup = this.meetings.getTeamStandUpLatch(Integer.parseInt(team)-1);
		int timeBefore = clock.getClock();
		try{
			teamStandup.await();
		} catch(InterruptedException e){
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		//First try to acquire the conference room
		try {
			boolean roomAvailable = available.tryAcquire();
			if(roomAvailable) {
				System.out.println(clock.getFormattedClock() + "  " + name + " secures a spot in the conference room.");
			} else {
				//If that didn't work then wait until it can be acquired
				System.out.println(clock.getFormattedClock() + "  " + name + " waits for the conference room to be available.");
				available.acquire();
				System.out.println(clock.getFormattedClock() + "  " + name + " secures a spot in the conference room.");
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		int timeAfter = clock.getClock();
		this.timeWorked += (timeAfter - timeBefore);
		
		//Wait for employees to enter the conference room
		try{
			teamStandup.await();
		} catch(InterruptedException e){
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		
		
		System.out.println(clock.getFormattedClock() + "  " + name + " hosts team standup meeting.");
		timeInMeetings += 15;
		this.timeLapseWorking(15);	
		System.out.println(clock.getFormattedClock() + "  " + name + " ends team standup meeting.");
		available.release();
		
	}
	
	@Override
	public void goToLunch(){
		synchronized (this) {
			isBusy = true;
		}
		super.goToLunch();
		synchronized (this) {
			isBusy = false;
			this.notifyAll();
		}
	}
	
	@Override
	public void workday(){
		this.arrive();
		this.goToManagerMeeting();
		this.goToTeamStandUpMeeting();
		
		int quesIndex = this.handleQuestionBeforeLunch();

		this.goToLunch();
		
		this.handleQuestionsAfterLunch(quesIndex);
		
		this.goToStatusMeeting();
		
		//Work the remainder of time if necessary
		if(480 > timeWorked){
			this.timeLapseWorking(480 - timeWorked);
		}
		this.leave();
	}

	private int handleQuestionBeforeLunch(){
		int index = 0;
		int questionTime = this.questionTimes.get(index);
		int timeBefore = clock.getClock();
		
		while(clock.getClock() < (this.lunchEndTime - this.timeAtLunch)){
			while (clock.getClock() < questionTime){
				if(clock.getClock() >= (this.lunchEndTime - this.timeAtLunch)){
					break;
				}
				//Answer all questions in the queue when there isn't a question to ask
				while(!this.questionQueue.isEmpty()){
					//Check if its lunch time and take a break if it is
					if(clock.getClock() >= (this.lunchEndTime - this.timeAtLunch)){
						System.out.println(clock.getFormattedClock() + "  " + name + " wants to go to lunch and will answer questions when he returns.");
						break;
					}
					Employee dev = this.questionQueue.remove();
					this.answerQuestion(dev);
				}
				
			}
			
			if(clock.getClock() >= questionTime) {
				this.questionAnswered = false;
				this.askQuestion();
				index++;
				questionTime = getQuestionTime(index);
				
			}
			
			
		}
		
		int timeAfter = clock.getClock();
		this.timeWorked += timeAfter-timeBefore;
		return index;
	}
	
	private void handleQuestionsAfterLunch(int quesIndex){
		//Process until 4pm (4pm = 480 simulated minutes)
		//Process until 4pm (4pm = 480 simulated minutes)
		int index = quesIndex;
		int questionTime = getQuestionTime(index);
		int timeBefore = clock.getClock();
		
		while(clock.getClock() < (480)){
			while (clock.getClock() < questionTime){
				if(clock.getClock() >= (480)){
					break;
				}
				//Answer all questions in the queue when there isn't a question to ask
				while(!this.questionQueue.isEmpty()){
					//Answer questions in the queue until 4
					//(answerQuestion will check if its 4pm)
					Employee dev = this.questionQueue.remove();
					this.answerQuestion(dev);
				}
				
			}
			
			if(clock.getClock() >= questionTime) {
				this.questionAnswered = false;
				this.askQuestion();
				index++;
				questionTime = getQuestionTime(index);
			}
		}
		
		int timeAfter = clock.getClock();
		this.timeWorked += timeAfter-timeBefore;		
	}
	
	private int getQuestionTime(int index){
		int questionTime;
		//Check the index and set the question time to a time that won't be reached
		//if there are no more questions
		if(this.questionTimes.size() <= index) {
			questionTime = 99999999;
		} else {
			questionTime = this.questionTimes.get(index);
		}
		
		return questionTime;
	}
}
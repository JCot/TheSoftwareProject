import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * 
 * @author Shannon
 *
 */
public class Employee extends Worker{
	protected String team;
	protected String devNumber;
	protected ArrayList<Integer> questionTimes;
	private TeamLead lead;
	private boolean questionAnswered;
	
	public Employee (String name, String devNumber, String teamNumber, TeamLead lead, Clock clock, CountDownLatch startLatch, MeetingController meetings) {
		super(name, clock, startLatch, meetings);

		this.devNumber = devNumber;
		this.team = teamNumber;
		this.lead = lead;
		this.arrivalTime = rand.nextInt(30); //Random time between 8 and 8:30
		this.lunchEndTime = rand.nextInt(480 - 240) + 240; //Random time between 12pm and 4pm
		this.timeAtLunch = rand.nextInt(30 - this.arrivalTime) + 30; //Random time between 30mins and 1 hour
		this.name = name;
		this.questionTimes = new ArrayList<Integer>();
		
		int numQuestions = 1;//rand.nextInt((Main.maxDevQues - Main.minDevQues) + 1) + Main.minDevQues;
		
		for(int i = 0; i < numQuestions; i++){
			questionTimes.add(rand.nextInt(480 - 90) + 90);
		}
		
		Collections.sort(questionTimes);
	}
	
	public synchronized void setQuestionAnswered(){
		this.questionAnswered = true;
	}
	
	//Ask team lead a question
	@Override
	public void askQuestion(){
		System.out.println(clock.getFormattedClock() + "  " + name + " gets in line to ask their team lead a question when the team lead is available next.");
		lead.getInLine(this);
		//Wait for the lead to get an answer
		synchronized(lead){
			while(this.questionAnswered == false){
				try {
					lead.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//Go to morning team stand-up meeting
	@Override
	public void goToTeamStandUpMeeting(){
		CyclicBarrier teamStandup = this.meetings.getTeamStandUpLatch(Integer.parseInt(team)-1);
		try{
			teamStandup.await(); //Wait for all team members to arrive to work and reach this point
		}
		catch(InterruptedException e){
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		//Wait for the team lead to secure the room and for all 
		//team members to arrive to the conference room
		try{
			teamStandup.await(); 
		}
		catch(InterruptedException e){
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		timeWorked += clock.getClock() - arrivalTime;
		System.out.println(clock.getFormattedClock() + "  " + name + " goes to team standup.");
		this.timeLapseWorking(15);
		timeInMeetings += 15;
	}
	
	public int getTimeWorked(){
		return timeWorked;
	}
	
	@Override
	public void workday() {
		//Start of day - employee arrives
		this.arrive();
		
		//Makes the employee wait until the manager meeting is over
		synchronized(this.meetings){
			while(!this.meetings.getManagerMeetingOver()){
				try {
					System.out.println(clock.getFormattedClock() + "  " + name + " works until team lead is finished with meeting with the manager.");
					this.meetings.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		this.goToTeamStandUpMeeting();
		
		//Ask questions and get index left off at
		int index = this.askQuestionsBeforeLunch();
		
		//Wait until lunch if all questions were answered
		synchronized(clock) {
			while (clock.getClock() < (this.lunchEndTime - this.timeAtLunch)) {
				try {
					clock.wait();
					this.timeWorked++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		this.goToLunch();
		
		//Ask remaining questions
		this.askQuestionsAfterLunch(index);
		int noMoreQuestions = clock.getClock();
		
		//4pm = 480 = 8 hours
		if(480 > noMoreQuestions){
			//Work until 4pm
			this.timeLapseWorking(480-noMoreQuestions);
		}
		
		this.goToStatusMeeting();
		if(480 > timeWorked){
			//Work remaining time
			this.timeLapseWorking(480 - timeWorked);
		}
		this.leave();
	}
	
	private int askQuestionsBeforeLunch() {
		int questionIndex = 0;
		for(int i = 0; i < questionTimes.size(); i++){
			//Define a before time for each iteration
			int clockBefore = clock.getClock();
			
			//If the question is meant to be asked later skip this function
			if(questionTimes.get(i) > this.lunchEndTime - this.timeAtLunch){
				questionIndex = i;
				break;
			}
			else{
				int askQuestionTime = questionTimes.get(i);
				questionIndex = i + 1;
				
				//Wait until it's question time
				synchronized(clock) {
					while (clock.getClock() < askQuestionTime) {
						try {
							clock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				//Don't ask anymore questions if it's time for lunch
				if(clock.getClock() >= this.lunchEndTime - this.timeAtLunch){
					int clockAfter = clock.getClock();
					this.timeWorked += (clockAfter - clockBefore);
					questionIndex = i;
					break;
				}
				
				//Add the question to the team lead queue
				this.questionAnswered = false;
				askQuestion();
			}
			//Add the time to complete each iteration
			int clockAfter = clock.getClock();
			this.timeWorked += (clockAfter - clockBefore);
		}
		return questionIndex;
	}
	
	
	private void askQuestionsAfterLunch(int lastIndex) {
		for(int i = lastIndex; i < questionTimes.size(); i++){
			//Define a before time for each iteration
			int clockBefore = clock.getClock();
			int askQuestionTime = questionTimes.get(i);

			//Wait until the question time
			synchronized(clock) {
				while (clock.getClock() < askQuestionTime) {
					try {
						clock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			//Don't ask anymore questions if it's time for the status meeting
			if(clock.getClock() >= 480){
				System.out.println(clock.getFormattedClock() + "  " + name + " wants to ask a question but will hold off until tomorrow.");
				int clockAfter = clock.getClock();
				this.timeWorked += (clockAfter - clockBefore);
				break;
			}
			
			//Add question to team lead queue
			this.questionAnswered = false;
			askQuestion();
			
			//Add the time to complete each iteration
			int clockAfter = clock.getClock();
			this.timeWorked += (clockAfter - clockBefore);
		}
	}
}

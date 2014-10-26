import java.util.ArrayList;
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
	
	public TeamLead(String name, String devNumber, String teamNumber, Clock clock, CountDownLatch start, MeetingController meetings, Manager manager){
		super(name, clock, start, meetings);
		this.team = teamNumber;
		this.devNumber = devNumber;
		this.arrivalTime = rand.nextInt(30);
		this.lunchEndTime = rand.nextInt(480 - 240) + 240;
		this.timeAtLunch = rand.nextInt(30 - this.arrivalTime) + 30;
		this.manager = manager;
	}
	
	public synchronized boolean isBusy(){
		return isBusy;
	}
	
	public synchronized void getInLine(Employee e){
		questionQueue.add(e);
	}
	
	//Try and answer a team members question
	public void answerQuestion(String askingName){
		boolean canAnswer = (rand.nextInt(1) == 1);
		synchronized (this) {
			isBusy = true;
		}
		//If it is greater than 4pm then any remaining questions must wait until tomorrow
		if(clock.getClock() >= 480){
			System.out.println(clock.getFormattedClock() + "  " + name + " requests that the question asked by " + askingName + " is held off until the next work day");
			synchronized (this) {
				isBusy = false;
				this.notifyAll();
			}
			return;
		}
		
		if(canAnswer){
			System.out.println(clock.getFormattedClock() + "  " + name + " answers a question for " + askingName);
			synchronized (this) {
				isBusy = false;
				this.notifyAll();
			}
			return;
		} else {
			System.out.println(clock.getFormattedClock() + "  " + name + " cannot answer " + askingName +"'s question.");
			this.askQuestion();
		}
		
	}
	
	@Override
	public void askQuestion(){
		synchronized (this) {
			isBusy = true;
		}
		if(manager.isBusy()){
			System.out.println(clock.getFormattedClock() + "  " + name + " waits in line to ask the manager a question");
		} else {
			System.out.println(clock.getFormattedClock() + "  " + name + " asks the manager a question");
		}
		manager.getInLine(this);
		synchronized(manager) {
			try {
				manager.wait();
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
		System.out.println(clock.getFormattedClock() + "  " + name + " waits for team members to arrive to work");
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
				System.out.println(clock.getFormattedClock() + "  " + name + " secures a spot in the conference room");
			} else {
				//If that didn't work then wait until it can be acquired
				System.out.println(clock.getFormattedClock() + "  " + name + " waits for the conference room to be available");
				available.acquire();
				System.out.println(clock.getFormattedClock() + "  " + name + " secures a spot in the conference room");
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
		
		
		
		System.out.println(clock.getFormattedClock() + "  " + name + " hosts team standup meeting");
		timeInMeetings += 15;
		this.timeLapseWorking(15);	
		System.out.println(clock.getFormattedClock() + "  " + name + " ends team standup meeting");
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
		
		//TODO asking questions
		while(clock.getClock() < (this.lunchEndTime - this.timeAtLunch)){
			while(this.questionQueue.isEmpty()){
				if(clock.getClock() >= (this.lunchEndTime - this.timeAtLunch)){
					break;
				}
				//Makes the employee work before going off to lunch
				synchronized(clock) {
					try {
						clock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					this.timeWorked++;
				}
			}
			while(!this.questionQueue.isEmpty()){
				if(clock.getClock() >= (this.lunchEndTime - this.timeAtLunch)){
					System.out.println(clock.getFormattedClock() + "  " + name + " wants to go to lunch and will answer questions when he returns");
					break;
				}
				int timeBefore = clock.getClock();
				String name = this.questionQueue.remove().name;
				this.answerQuestion(name);
				int timeAfter = clock.getClock();
				this.timeWorked += timeAfter-timeBefore;
			}
		}

		
		this.goToLunch();
		
		
		//4pm = 480
		while(clock.getClock() < (480)){
			while(this.questionQueue.isEmpty()){
				if(clock.getClock() >= 480) {
					break;
				}
				//Makes the employee work
				synchronized(clock) {
					try {
						clock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					this.timeWorked++;
				}
			}
			while(!this.questionQueue.isEmpty()){
				int timeBefore = clock.getClock();
				String name = this.questionQueue.remove().name;
				this.answerQuestion(name);
				int timeAfter = clock.getClock();
				this.timeWorked += timeAfter-timeBefore;
			}
		}
		
		//this.timeLapseWorking(480-backFromLunch);
		this.goToStatusMeeting();
		if(480 > timeWorked){
			this.timeLapseWorking(480 - timeWorked);
		}
		this.leave();
	}

}
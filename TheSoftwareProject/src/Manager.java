import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author Shannon
 *
 */
public class Manager extends Worker{
	private List<Thread> employees;
	private List<Thread> teamLeads;
	private boolean isBusy;
	private LinkedList<TeamLead> questionQueue = new LinkedList<TeamLead>();
	
	/**
	 * accumulate statistics on the total amount of time across the manager
	 *  and all his developers 
	 *  	(a) working, 
	 *  	(b) at lunch, 
	 *  	(c) in meetings, and 
	 *  	(d) waiting for the manager to be free to answer a question.
	 * @param name
	 */
	public Manager(String name, Clock clock, CountDownLatch startLatch, MeetingController meetings) {
		super(name, clock, startLatch, meetings);
		this.arrivalTime = 0;
		this.timeAtLunch = 60;// 1 hour
		this.lunchEndTime = 300;// 1pm
		employees = new ArrayList<Thread>();
		teamLeads = new ArrayList<Thread>();
	}
	
	public void addEmployee(Employee e) {
		employees.add(e);
	}
	
	public void addTeamLead(TeamLead l){
		teamLeads.add(l);
	}
	
	public synchronized boolean isBusy(){
		return isBusy;
	}
	
	public synchronized void getInLine(TeamLead lead){
		questionQueue.add(lead);
	}
	
	public synchronized boolean isFirst(TeamLead lead){
		return lead.equals(questionQueue.getFirst());
	}
	
	/**
	 * this is the meeting with all the team leads in the morning
	 */
	public void startStandUpMeeting() {
		CountDownLatch standup = this.meetings.getManagerMeeting();
		standup.countDown();
		
		int timeBefore = clock.getClock();
		try {
			//waiting for everyone to get here.
			standup.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int timeAfter = clock.getClock();
		this.timeWorked = (timeAfter - timeBefore);
		
		System.out.println(clock.getFormattedClock() + "  " + name + " starts the morning standup.");
		//wait(minute * 15);  //the meeting lasts 15 minutes
		this.timeLapseWorking(15);
		System.out.println(clock.getFormattedClock() + "  " + name + " ends the morning standup.");
		this.meetings.setManagerMeetingOver();
		synchronized(this.meetings) {
			this.meetings.notifyAll();
		}
	}
	
	/**
	 * There are 2 meetings, one at 10 and one at 2, they are both 1 hour
	 */
	public void goToMeeting(){
		synchronized(this){
			isBusy = true;
		}
		System.out.println(clock.getFormattedClock() + "  " + name + " goes to a meeting");
		this.timeLapseWorking(60); 
		System.out.println(clock.getFormattedClock() + "  " + name + " returns from a meeting");
		synchronized(this){
			isBusy = false;
			this.notifyAll();
		}
	}
	
	@Override
	public void goToLunch(){
		synchronized(this){
			isBusy = true;
		}
		super.goToLunch();
		synchronized(this){
			isBusy = false;
			this.notifyAll();
		}
	}
	
	public void answerQuestion(TeamLead askingThread) {
		//If it is greater than 4pm then any remaining questions must wait until tomorrow
		if(clock.getClock() >= 480){
			System.out.println(clock.getFormattedClock() + "  " + name + " requests that the question asked by " + askingThread.name + " is held off until the next work day");
			synchronized(this){
				askingThread.setQuestionAnswered();
				isBusy = false;
				this.notifyAll();
			}
			return;
		} else {
			synchronized(this){
				isBusy = true;
			}
			System.out.println(clock.getFormattedClock() + "  " + name + " is asked a question by " + askingThread.name);
			this.timeLapseWorking(10);
			System.out.println(clock.getFormattedClock() + "  " + name + " finished answering a question for " + askingThread.name);
			synchronized(this){
				askingThread.setQuestionAnswered();
				isBusy = false;
				this.notifyAll();
			}
			return;
		}
	}
	
	/**
	 * This is where I'm not really sure how to actually make sure that these are the
	 * times all these things are happening
	 */
	public void workday() {
		this.arrive(); //8 AM
		System.out.println(clock.getFormattedClock() + "  " + name + " performs planning and administrative activities");
		this.startStandUpMeeting(); //ASAP
		
		//10am = 120 minutes past 8am
		while(clock.getClock() < (120)){
			while(this.questionQueue.isEmpty()){
				if(clock.getClock() >= 120) {
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
				if(clock.getClock() >= 120) {
					System.out.println(clock.getFormattedClock() + "  " + name + " has a meeting to go to and will continue answering questions later");
					break;
				}
				int timeBefore = clock.getClock();
				TeamLead lead = this.questionQueue.remove();
				this.answerQuestion(lead);
				int timeAfter = clock.getClock();
				this.timeWorked += timeAfter-timeBefore;
			}
		}
		
		this.goToMeeting(); //10 AM
		
		while(clock.getClock() < (this.lunchEndTime - this.timeAtLunch)){
			while(this.questionQueue.isEmpty()){
				if(clock.getClock() >= (this.lunchEndTime - this.timeAtLunch)) {
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
				if(clock.getClock() >= (this.lunchEndTime - this.timeAtLunch)) {
					System.out.println(clock.getFormattedClock() + "  " + name + " wants to go to lunch and will continue answering questions later");
					break;
				}
				int timeBefore = clock.getClock();
				TeamLead lead = this.questionQueue.remove();
				this.answerQuestion(lead);
				int timeAfter = clock.getClock();
				this.timeWorked += timeAfter-timeBefore;
			}
		}
		
	
		this.goToLunch(); //12 PM

		
		//2pm = 360 minutes past 8am
		while(clock.getClock() < (360)){
			while(this.questionQueue.isEmpty()){
				if(clock.getClock() >= 360) {
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
				if(clock.getClock() >= 360) {
					System.out.println(clock.getFormattedClock() + "  " + name + " has a meeting to go to and will continue answering questions later");
					break;
				}
				int timeBefore = clock.getClock();
				TeamLead lead = this.questionQueue.remove();
				this.answerQuestion(lead);
				int timeAfter = clock.getClock();
				this.timeWorked += timeAfter-timeBefore;
			}
		}
		//this.timeLapseWorking(360 - lunchEnd);
		this.goToMeeting(); //2PM
		//int meetingTwoEnd = clock.getClock();
		
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
				TeamLead lead = this.questionQueue.remove();
				this.answerQuestion(lead);
				int timeAfter = clock.getClock();
				this.timeWorked += timeAfter-timeBefore;
			}
		}
		
		//this.timeLapseWorking(480 - meetingTwoEnd);
		this.goToStatusMeeting(); //4PM
		int statusEnd = clock.getClock();
		
		//The manager will always stil until 5pm no matter what
		this.timeLapseWorking(540 - statusEnd);
		this.leave();
	}
	
	public int totalTimeWorking() {
		return 0;
		
	}
	
	public int totalTimeLunch() {
		return 0;
		
	}
	
	public int totalTimeMeetings() {
		return 0;
		
	}
	
	public int totalTimeWaiting() {
		return 0;
	}

	@Override
	public void goToTeamStandUpMeeting() {
		// Does nothing for manager
		
	}

	@Override
	public void askQuestion() {
		// Does nothing for manager
		
	}
	
}

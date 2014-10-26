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
	
	@Override
	public void goToTeamStandUpMeeting() {
		// Does nothing for manager
		
	}

	@Override
	public void askQuestion() {
		// Does nothing for manager
		
	}
	
	/**
	 * This is where I'm not really sure how to actually make sure that these are the
	 * times all these things are happening
	 */
	public void workday() {
		this.arrive(); //8 AM
		System.out.println(clock.getFormattedClock() + "  " + name + " performs planning and administrative activities.");
		this.startStandUpMeeting(); //ASAP
		
		this.receiveQuestionsBeforeFirstMeeting();
		this.goToMeeting(); //10 AM
		
		
		this.receiveQuestionsBeforeLunch();
		this.goToLunch(); //12 PM

		this.receiveQuestionsBeforeSecondMeeting();
		this.goToMeeting(); //2PM
		
		this.receiveQuestionsBeforeStatusMeeting();
		this.goToStatusMeeting(); //4PM
		
		//The manager will always work until 5pm no matter what
		int statusEnd = clock.getClock();
		this.timeLapseWorking(540 - statusEnd);
		this.leave();
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
		this.timeLapseWorking(15);
		this.timeInMeetings += 15 + (timeAfter - timeBefore);
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
		System.out.println(clock.getFormattedClock() + "  " + name + " goes to a meeting.");
		this.timeLapseWorking(60); 
		this.timeInMeetings += 60;
		System.out.println(clock.getFormattedClock() + "  " + name + " returns from a meeting.");
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
		//String to include in text if the dev parameter isnt empty
		String includeDev = !askingThread.devAsking.equals("") ? " & " + askingThread.devAsking : "";
		
		//If it is greater than 4pm then any remaining questions must wait until tomorrow
		if(clock.getClock() >= 480){
			System.out.println(clock.getFormattedClock() + "  " + name + " requests that the question asked by " + askingThread.name + includeDev + " is held off until the next work day.");
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
			System.out.println(clock.getFormattedClock() + "  " + name + " is asked a question by " + askingThread.name + includeDev + ".");
			//The manager's work time is already accounted for here so we just need some time to lapse
			this.timeLapse(Main.managerQuesTime);
			System.out.println(clock.getFormattedClock() + "  " + name + " finished answering a question for " + askingThread.name + includeDev + ".");
			synchronized(this){
				askingThread.setQuestionAnswered();
				isBusy = false;
				this.notifyAll();
			}
			return;
		}
	}
	
	@Override
	public void goToStatusMeeting() {
		while(!this.questionQueue.isEmpty()){
			//Address all questions before going to the status meeting
			TeamLead lead = this.questionQueue.remove();
			this.answerQuestion(lead);
		}
		super.goToStatusMeeting();
	}
	
	private void receiveQuestionsBeforeFirstMeeting() {
		//10am = 120 minutes past 8am
		while(clock.getClock() < (120)){
			while(this.questionQueue.isEmpty()){
				//Break so the manager can go to his meeting
				if(clock.getClock() >= 120) {
					break;
				}
				//Makes the manager work
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
				//Make the manager go to his meeting and ask questions later
				if(clock.getClock() >= 120) {
					System.out.println(clock.getFormattedClock() + "  " + name + " has a meeting to go to and will continue answering questions later.");
					break;
				}
				int timeBefore = clock.getClock();
				TeamLead lead = this.questionQueue.remove();
				this.answerQuestion(lead);
				int timeAfter = clock.getClock();
				this.timeWorked += timeAfter-timeBefore;
			}
		}
	}
	
	private void receiveQuestionsBeforeLunch() {
		//While it is not the manager's lunch time
		while(clock.getClock() < (this.lunchEndTime - this.timeAtLunch)){
			while(this.questionQueue.isEmpty()){
				//Break for lunch
				if(clock.getClock() >= (this.lunchEndTime - this.timeAtLunch)) {
					break;
				}
				//Makes the manager work
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
				//Have the manager take a break for lunch then continue to answer questions
				if(clock.getClock() >= (this.lunchEndTime - this.timeAtLunch)) {
					System.out.println(clock.getFormattedClock() + "  " + name + " wants to go to lunch and will continue answering questions later.");
					break;
				}
				int timeBefore = clock.getClock();
				TeamLead lead = this.questionQueue.remove();
				this.answerQuestion(lead);
				int timeAfter = clock.getClock();
				this.timeWorked += timeAfter-timeBefore;
			}
		}
	}
	
	private void receiveQuestionsBeforeSecondMeeting() {
		//2pm = 360 minutes past 8am
		while(clock.getClock() < (360)){
			while(this.questionQueue.isEmpty()){
				//Break for the meeting
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
				//Hold off answering questions until after the meeting
				if(clock.getClock() >= 360) {
					System.out.println(clock.getFormattedClock() + "  " + name + " has a meeting to go to and will continue answering questions later.");
					break;
				}
				int timeBefore = clock.getClock();
				TeamLead lead = this.questionQueue.remove();
				this.answerQuestion(lead);
				int timeAfter = clock.getClock();
				this.timeWorked += timeAfter-timeBefore;
			}
		}
	}
	
	private void receiveQuestionsBeforeStatusMeeting() {
		while(clock.getClock() < (480)){
			while(this.questionQueue.isEmpty()){
				//Break for the status meeting
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
				//answerQuestion checks if it is 4pm, and will return accordingly
				this.answerQuestion(lead);
				int timeAfter = clock.getClock();
				this.timeWorked += timeAfter-timeBefore;
			}
		}
	}
}

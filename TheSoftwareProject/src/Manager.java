import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author Shannon
 *
 */
public class Manager extends Worker{
	private List<Thread> employees;
	
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
	}
	
	public void addEmployee(Employee e) {
		employees.add(e);
	}
	
	public void answerQuestion() {
		System.out.println(clock.getFormattedClock() + name + " answers a question");
		try {
			wait(minute * 10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * This is where I'm not really sure how to actually make sure that these are the
	 * times all these things are happening
	 */
	public void workday() {
		this.arrive(); //8 AM
		System.out.println(clock.getFormattedClock() + "  " + name + " performs planning and administrative activities");
		this.startStandUpMeeting(); //ASAP
		int standupEnd = clock.getClock();
		
		//10am = 120 minutes past 8am
		this.timeLapse(120 - standupEnd);// Wait until 10am
		this.goToMeeting(); //10 AM
		int meetingOneEnd = clock.getClock();
		
		//Work until lunch
		this.goToLunch(); //12 PM
		int lunchEnd = clock.getClock();
		
		//2pm = 360 minutes past 8am
		this.timeLapse(360 - lunchEnd);
		this.goToMeeting(); //2PM
		int meetingTwoEnd = clock.getClock();
		
		this.timeLapse(480 - meetingTwoEnd);
		this.goToStatusMeeting(); //4PM
		int statusEnd = clock.getClock();
		
		this.timeLapse(540 - statusEnd);
		this.leave();
	}
	
	/**
	 * There are 2 meetings, one at 10 and one at 2, they are both 1 hour
	 */
	public void goToMeeting(){
		System.out.println(clock.getFormattedClock() + "  " + name + " goes to a meeting");
		this.timeLapse(60); 
		System.out.println(clock.getFormattedClock() + "  " + name + " returns from a meeting");
	}
	
	
	
	/**
	 * this is the meeting with all the team leads in the morning
	 */
	public void startStandUpMeeting() {
		CountDownLatch standup = this.meetings.getManagerMeeting();
		standup.countDown();
		try {
			//waiting for everyone to get here.
			standup.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(clock.getFormattedClock() + "  " + name + " starts the morning standup.");
		//wait(minute * 15);  //the meeting lasts 15 minutes
		this.timeLapse(15);
		System.out.println(clock.getFormattedClock() + "  " + name + " ends the morning standup.");
		synchronized(standup) {
			standup.notifyAll();
		}
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
	
}

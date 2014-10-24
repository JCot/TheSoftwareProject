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
	private CountDownLatch standUpLatch;
	
	/**
	 * accumulate statistics on the total amount of time across the manager
	 *  and all his developers 
	 *  	(a) working, 
	 *  	(b) at lunch, 
	 *  	(c) in meetings, and 
	 *  	(d) waiting for the manager to be free to answer a question.
	 * @param name
	 */
	public Manager(String name, Clock clock, CountDownLatch latch, CountDownLatch statusMeetingLatch, CountDownLatch standUpLatch) {
		super(name, clock, latch, statusMeetingLatch);
		this.arrivalTime = 0;
		this.timeAtLunch = 60;
		employees = new ArrayList<Thread>();
		this.standUpLatch = standUpLatch;
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
		this.startStandUpMeeting(); //ASAP
		this.goToMeeting(); //10 AM
		this.goToLunch(); //12 PM
		this.goToMeeting(); //2PM
		this.goToStatusMeeting(); //4PM
	}
	
	/**
	 * There are 2 meetings, one at 10 and one at 2, they are both 1 hour
	 */
	public void goToMeeting(){
		System.out.println(clock.getFormattedClock() + " " + name + " goes to a meeting");
		try {
			synchronized(clock){
				int time = clock.getClock();
				while(clock.getClock() <= time + 60){
					clock.wait();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * this is the meeting with all the team leads in the morning
	 */
	public void startStandUpMeeting() {
		standUpLatch.countDown();
		try {
			standUpLatch.await();  //waiting for everyone to get here.
			System.out.println(clock.getFormattedClock() + " " + name + " starts the morning stand up.");
			//wait(minute * 15);  //the meeting lasts 15 minutes
			synchronized(clock){
				int time = clock.getClock();
				while(clock.getClock() <= time + 15){
					try{
						clock.wait();
					}
					catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(clock.getFormattedClock() + " " + name + " ends the morning stand up.");
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

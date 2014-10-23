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
	private final int day = 4800; //milliseconds
	private final int minute = 10; //milliseconds
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
		employees = new ArrayList<Thread>();
		this.standUpLatch = standUpLatch;
	}
	
	public void addEmployee(Employee e) {
		employees.add(e);
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
	
	public void workday() {
		this.arrive();
	}
}

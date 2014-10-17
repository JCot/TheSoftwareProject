import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Shannon
 *
 */
public class Manager extends Thread{
	private List<Thread> employees;
	private final int day = 4800; //milliseconds
	private final int minute = 10; //milliseconds
	
	/**
	 * accumulate statistics on the total amount of time across the manager
	 *  and all his developers 
	 *  	(a) working, 
	 *  	(b) at lunch, 
	 *  	(c) in meetings, and 
	 *  	(d) waiting for the manager to be free to answer a question.
	 * @param name
	 */
	public Manager(String name) {
		super(name);
		employees = new ArrayList<Thread>();
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

}

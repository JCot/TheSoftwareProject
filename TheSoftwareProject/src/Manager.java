import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Shannon
 *
 */
public class Manager extends Thread{
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
	public Manager(String name) {
		super(name);
		employees = new ArrayList<Thread>();
	}
	
	public void addEmployee(Employee e) {
		employees.add(e);
	}

}

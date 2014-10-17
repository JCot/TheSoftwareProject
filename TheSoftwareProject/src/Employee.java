/**
 * 
 * @author Shannon
 *
 */
public class Employee extends Thread{
	private int team;
	private final int day = 4800; //milliseconds
	private final int minute = 10; //milliseconds
	
	public Employee (String name, int t) {
		super(name);
		team = t;
	}

}

/**
 * 
 * @author Shannon
 *
 */
public class Employee extends Thread{
	private int team;
	
	public Employee (String name, int t) {
		super(name);
		team = t;
	}

}

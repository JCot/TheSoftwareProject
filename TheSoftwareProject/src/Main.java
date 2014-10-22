/**
 * 
 * @author Shannon
 *
 */
public class Main {
	private final static int day = 5400; //milliseconds, (9 hour work day)
	private final static int minute = 10; //milliseconds

	public static void main(String[] args) {
		Manager bob = new Manager("Bob");
		
		Clock clock = new Clock();
		Thread timer = new Thread(new Timer(minute,day,clock));
		
		//Developer NM, where N is the team number (1-3)
		//and M is the employee's number on the team (1-4,
		//where 1 is the team lead)
		for (int i = 1; i <= 3; i++) {
			for (int j = 1; j<=4; j++) {
				Employee e = new Employee("Developer " + i + j, Integer.toString(j), Integer.toString(i), clock);
				bob.addEmployee(e);
			}
		}
		
		//TBD
	}
}

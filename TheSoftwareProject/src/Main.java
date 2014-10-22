import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author Shannon
 *
 */
public class Main {
	private final static int day = 5400; //milliseconds, (9 hour work day)
	private final static int minute = 10; //milliseconds
	
	public static void main(String[] args) {
		//14 Total threads will run within this application
		CountDownLatch startLatch = new CountDownLatch(14);
		Clock clock = new Clock();
		List<Thread> employees = new ArrayList<Thread>();
		Thread timer = new Thread(new Timer(minute,day,clock,startLatch));
		Manager bob = new Manager("Bob", clock, startLatch);
		
		
		//Developer NM, where N is the team number (1-3)
		//and M is the employee's number on the team (1-4,
		//where 1 is the team lead)
		for (int i = 1; i <= 3; i++) {
			for (int j = 1; j<=4; j++) {
				Employee e = new Employee("Developer " + i + j, Integer.toString(j), Integer.toString(i), clock, startLatch);
				employees.add(e);
				bob.addEmployee(e);
			}
		}
		
		timer.start();
		bob.start();
		for(Thread e : employees) {
			e.start();
		}
		
		
		
		//TBD
	}
}

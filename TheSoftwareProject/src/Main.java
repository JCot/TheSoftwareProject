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
		CountDownLatch startLatch = new CountDownLatch(1);
		//The status meeting at the end of the day will involve all 13 employees
		CountDownLatch statusMeetingLatch = new CountDownLatch(13);
		//The meeting between the manager and the team leads will involve 5 people
		CountDownLatch standUpLatch = new CountDownLatch(5);
		Clock clock = new Clock();
		List<Thread> employees = new ArrayList<Thread>();
		Thread timer = new Thread(new Timer(minute,day,clock,startLatch));
		Manager bob = new Manager("Bob", clock, startLatch, statusMeetingLatch, standUpLatch);
		
		
		//Developer NM, where N is the team number (1-3)
		//and M is the employee's number on the team (1-4,
		//where 1 is the team lead)
		for (int i = 1; i <= 3; i++) {
			CountDownLatch teamStandUpLatch = new CountDownLatch(4);
			for (int j = 1; j<=4; j++) {
				if(j == 1){
					TeamLead l = new TeamLead("Developer " + i + j, Integer.toString(j), Integer.toString(i), clock, startLatch, statusMeetingLatch, teamStandUpLatch, standUpLatch);
					employees.add(l);
					bob.addEmployee(l);
				}
				else{
					Employee e = new Employee("Developer " + i + j, Integer.toString(j), Integer.toString(i), clock, startLatch, statusMeetingLatch, teamStandUpLatch);
					employees.add(e);
					bob.addEmployee(e);
				}
			}
		}
		
		timer.start();
		bob.start();
		for(Thread e : employees) {
			e.start();
		}
		
		startLatch.countDown();
		
		
		
		//TBD
	}
}

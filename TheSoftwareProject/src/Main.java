import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author Shannon
 *
 */
public class Main {
	private final static int day = 6000; //milliseconds, (9 hour work day)
	private final static int minute = 10; //milliseconds
	
	private final static int numTeams = 3;
	private final static int numDevsPerTeam = 4;
	
	public static void main(String[] args) {
		CountDownLatch startLatch = new CountDownLatch(1);
		MeetingController meetings = new MeetingController(numTeams, numDevsPerTeam);
		Clock clock = new Clock();
		List<Thread> employees = new ArrayList<Thread>();
		Thread timer = new Thread(new Timer(minute,day,clock,startLatch));
		Manager bob = new Manager("Bob", clock, startLatch, meetings);
		
		
		//Developer NM, where N is the team number (1-3)
		//and M is the employee's number on the team (1-4,
		//where 1 is the team lead)
		for (int i = 1; i <= numTeams; i++) {
			TeamLead lead = new TeamLead("Developer " + i + "1", "1", Integer.toString(i), clock, startLatch, meetings);
			employees.add(lead);
			bob.addTeamLead(lead);
			for (int j = 2; j<=numDevsPerTeam; j++) { 
				Employee dev = new Employee("Developer " + i + j, Integer.toString(j), Integer.toString(i), lead, clock, startLatch, meetings);
				employees.add(dev);
				bob.addEmployee(dev);
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

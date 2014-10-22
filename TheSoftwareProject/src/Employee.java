import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * 
 * @author Shannon
 *
 */
public class Employee extends Worker{
	protected String team;
	protected String devNumber;
	protected String name;
	protected String teamLead;
	protected int arrivalTime;
	protected int lunchEndTime;
	protected int timeAtLunch;
	protected Clock clock;
	
	public Employee (String name, String devNumber, String teamNumber, Clock clock, CountDownLatch startLatch) {
		super(name, clock, startLatch);
		this.devNumber = devNumber;
		this.team = teamNumber;
		this.teamLead = "Developer " + team + "1";
		this.arrivalTime = rand.nextInt(30);
		this.lunchEndTime = rand.nextInt(480 - 240) + 240;
		this.timeAtLunch = rand.nextInt(30 - this.arrivalTime) + 30;
		this.clock = clock;
		this.name = name;
	}
	
	//Ask team lead a question
	public void askQuestion(){
		System.out.println(clock.getFormattedClock() + name + " asks team lead a question");
	}
	
	public void goToLunch(){
		System.out.println(clock.getFormattedClock() + name + " goes to lunch");
	}
	
	public void leave(){
		System.out.println(clock.getFormattedClock() + name + " leaves work");
	}
	
	//Go to the end of the day status meeting
	public void goToStatusMeeting(){
		System.out.println(clock.getFormattedClock() + name + " goes to daily status meeting");
	}
	
	//Go to morning team stand-up meeting
	public void goToStandUpMeeting(){
		System.out.println(clock.getFormattedClock() + name + " goes to team standup");
	}
	
	@Override
	public void workday() {
		//Start of day - employee arrives
		this.arrive();
	}
}

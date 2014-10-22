import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * 
 * @author Shannon
 *
 */
public class Employee extends Thread{
	protected String team;
	protected String devNumber;
	protected String name;
	protected String teamLead;
	protected int arrivalTime;
	protected int lunchEndTime;
	protected int timeAtLunch;
	protected Clock clock;
	protected Random rand = new Random();
	
	protected static final int NUM_CONFERENCE_ROOMS = 1;
	protected static final Semaphore available = new Semaphore(NUM_CONFERENCE_ROOMS, true);
	
	public Employee (String name, String devNumber, String teamNumber, Clock clock) {
		super(name);
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
	
	public void arrive(){
		System.out.println(clock.getFormattedClock() + name + " arrives at work");
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
	
	public void run(){
		
	}
}

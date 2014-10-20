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
	protected int arrivalTime;
	//protected TeamLead lead;
	protected Clock clock;
	
	protected static final int NUM_CONFERENCE_ROOMS = 1;
	protected static final Semaphore available = new Semaphore(NUM_CONFERENCE_ROOMS, true);
	
	public Employee (String devNumber, String teamNumber, int arrivalTime, Clock clock) {
		super(devNumber);
		this.devNumber = devNumber;
		this.team = teamNumber;
		this.arrivalTime = arrivalTime;
		//this.lead = lead;
		this.clock = clock;
		this.name = "Developer " + this.team + this.devNumber;
	}
	
	//Ask team lead a question
	public void askQuestion(){
		System.out.println(clock.getFormattedClock() + " Developer" + team + devNumber + " asks team lead a question");
		//lead.answerQuestion();
	}
	
	public void goToLunch(){
		System.out.println(clock.getFormattedClock() + " Developer" + team + devNumber + " goes to lunch");
	}
	
	public void arrive(){
		System.out.println(clock.getFormattedClock() + " Developer" + team + devNumber + " arrives at work");
	}
	
	public void leave(){
		System.out.println(clock.getFormattedClock() + " Developer" + team + devNumber + " leaves work");
	}
	
	//Go to the end of the day status meeting
	public void goToStatusMeeting(){
		System.out.println(clock.getFormattedClock() + " Developer" + team + devNumber + " goes to daily status meeting");
	}
	
	//Go to morning team stand-up meeting
	public void goToStandUpMeeting(){
		System.out.println(clock.getFormattedClock() + " Developer" + team + devNumber + " goes to team standup");
	}

}

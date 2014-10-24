import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author Shannon
 *
 */
public class Employee extends Worker{
	protected String team;
	protected String devNumber;
	protected String teamLead;
	
	public Employee (String name, String devNumber, String teamNumber, Clock clock, CountDownLatch startLatch, MeetingController meetings) {
		super(name, clock, startLatch, meetings);

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
		System.out.println(clock.getFormattedClock() + " " + name + " asks team lead a question");
	}
	
	//Go to morning team stand-up meeting
	public void goToTeamStandUpMeeting(){
		CountDownLatch teamStandup = this.meetings.getTeamStandUpLatch(Integer.parseInt(team)-1);
		timeWorked += clock.getClock() - arrivalTime;
		System.out.println(clock.getFormattedClock() + "  " + name + " goes to team standup");
		teamStandup.countDown();
		try{
			teamStandup.await();
			timeInMeetings += 15;
			wait();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public int getTimeWorked(){
		return timeWorked;
	}
	
	@Override
	public void workday() {
		//Start of day - employee arrives
		this.arrive();
		//System.out.println(name + " works until team lead is finished with meeting with the manager");
		//this.goToTeamStandUpMeeting();
		//TODO figure out timing, asking questions
		//this.goToLunch();
		//this.goToStatusMeeting();
		//this.leave();
	}
}

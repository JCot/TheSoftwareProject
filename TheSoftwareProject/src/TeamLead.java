import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class TeamLead extends Employee {
	private ArrayList<Employee> teamMembers;//May not be needed
	
	public TeamLead(String name, String devNumber, String teamNumber, Clock clock, CountDownLatch start, CountDownLatch statusMeetingLatch, CountDownLatch teamStandUpLatch){
		super(name, devNumber, teamNumber, clock, start, statusMeetingLatch, teamStandUpLatch);
		teamMembers = new ArrayList<Employee>();
	}
	
	//Try and answer a team members question
	public void answerQuestion(){
		boolean canAnswer = (rand.nextInt(1) == 1);
		
		if(canAnswer){
			System.out.println(clock.getFormattedClock() + name + " answers a question");
			return;
		}
		
		else{
			System.out.println(clock.getFormattedClock() + name + " cannot answer a question. Takes question to manager");
			//lead.askQuestion();
		}
	}
	
	@Override
	public void askQuestion(){
		System.out.println(clock.getFormattedClock() + name + " asks the manager a question");
	}
	
	//Go to morning meeting with PM
	public void goToManagerMeeting(){
		System.out.println(clock.getFormattedClock() + name + " goes to standup meeting with manager");
	}
	
	@Override
	public void goToTeamStandUpMeeting(){
		try{
			available.acquire();
		}
		catch(InterruptedException e){}
	}
	
	@Override
	public void workday(){
		this.arrive();
	}

}
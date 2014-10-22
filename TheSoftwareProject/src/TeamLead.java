import java.util.ArrayList;
import java.util.Random;


public class TeamLead extends Employee {
	private ArrayList<Employee> teamMembers;
	
	public TeamLead(String name, String devNumber, String teamNumber, Clock clock){
		super(name, devNumber, teamNumber, clock);
		teamMembers = new ArrayList<Employee>();
	}
	
	//Try and answer a team members question
	public void answerQuestion(){
		boolean canAnswer = (rand.nextInt(1) == 1);
		
		if(canAnswer){
			System.out.println(clock.getFormattedClock() + " Developer" + team + devNumber + " answers a question");
			return;
		}
		
		else{
			System.out.println(clock.getFormattedClock() + " Developer" + team + devNumber + " cannot answer a question. Takes question to manager");
			//lead.askQuestion();
		}
	}
	
	@Override
	public void askQuestion(){
		System.out.println(clock.getFormattedClock() + " Developer" + team + devNumber + " asks the manager a question");
	}
	
	//Go to morning meeting with PM
	public void goToManagerMeeting(){
		System.out.println(clock.getFormattedClock() + " Developer" + team + devNumber + " goes to standup meeting with manager");
	}
	
	@Override
	public void goToStandUpMeeting(){
		try{
			available.acquire();
		}
		catch(InterruptedException e){}
	}

}
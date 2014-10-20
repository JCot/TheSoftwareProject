import java.util.Random;


public class TeamLead extends Employee {
	Random rand = new Random();
	
	public TeamLead(String devNumber, String teamNumber, int arrivalTime, Clock clock){
		super(devNumber, teamNumber, arrivalTime, clock);
	}
	
	//Try and answer a team members question
	public void answerQuestion(){
		//TODO change to be a 50/50 chance
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
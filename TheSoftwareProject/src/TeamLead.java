import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class TeamLead extends Employee {
	private ArrayList<Employee> teamMembers;//May not be needed
	private CountDownLatch managerMeetingLatch;
	
	public TeamLead(String name, String devNumber, String teamNumber, Clock clock, CountDownLatch start, CountDownLatch statusMeetingLatch, CountDownLatch teamStandUpLatch, CountDownLatch managerMeetingLatch){
		super(name, devNumber, teamNumber, clock, start, statusMeetingLatch, teamStandUpLatch);
		teamMembers = new ArrayList<Employee>();
		this.managerMeetingLatch = managerMeetingLatch;
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
		System.out.println(clock.getFormattedClock() + " " + name + " asks the manager a question");
	}
	
	//Go to morning meeting with PM
	public void goToManagerMeeting(){
		System.out.println(clock.getFormattedClock() + " " + name + " knocks on the managers door.");
		
		this.managerMeetingLatch.countDown();
		try{
			this.managerMeetingLatch.await();
			timeInMeetings += 15;
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void goToTeamStandUpMeeting(){
		System.out.println(name + " waits for team members to arrive");
		teamStandUpLatch.countDown();
		try{
			teamStandUpLatch.await();
			available.acquire();
			System.out.println(clock.getFormattedClock() + " " + name + " hosts team standup meeting");
			timeInMeetings += 15;
			//sleep for some time;
			notifyAll();
		}
		catch(InterruptedException e){}
	}
	
	@Override
	public void workday(){
		this.arrive();
		this.goToManagerMeeting();
		this.goToTeamStandUpMeeting();
	}

}
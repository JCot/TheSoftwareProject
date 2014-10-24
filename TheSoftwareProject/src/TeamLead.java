import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class TeamLead extends Employee {
	private ArrayList<Employee> teamMembers;//May not be needed
	private String team;
	
	public TeamLead(String name, String devNumber, String teamNumber, Clock clock, CountDownLatch start, MeetingController meetings){
		super(name, devNumber, teamNumber, clock, start, meetings);
		this.team = teamNumber;
		teamMembers = new ArrayList<Employee>();
	}
	
	public void addEmployee(Employee e){
		teamMembers.add(e);
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
			askQuestion();
		}
	}
	
	@Override
	public void askQuestion(){
		System.out.println(clock.getFormattedClock() + " " + name + " asks the manager a question");
	}
	
	//Go to morning meeting with PM
	public void goToManagerMeeting(){
		CountDownLatch managerStandup = this.meetings.getManagerMeeting();
		System.out.println(clock.getFormattedClock() + "  " + name + " knocks on the manager's door.");
		
		managerStandup.countDown();
		try{
			managerStandup.await();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		this.timeLapse(15);
		
		timeInMeetings += 15;
	}
	
	@Override
	public void goToTeamStandUpMeeting(){
		System.out.println(clock.getFormattedClock() + " " + name + " waits for team members to arrive");
		CountDownLatch managerStandup = this.meetings.getTeamStandUpLatch(Integer.parseInt(team)-1);
		managerStandup.countDown();
		try{
			managerStandup.await();
		} catch(InterruptedException e){}
		System.out.println(clock.getFormattedClock() + " " + name + " waits for the conference room to be available");
		try {
			available.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println(clock.getFormattedClock() + " " + name + " hosts team standup meeting");
		timeInMeetings += 15;
		synchronized(clock){
			int time = clock.getClock();
			while(clock.getClock() <= time + 15){
				try{
					clock.wait();
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		for(Employee e: teamMembers){
			e.notify();
		}
		available.release();
		
	}
	
	@Override
	public void workday(){
		this.arrive();
		this.goToManagerMeeting();
		this.goToTeamStandUpMeeting();
		
		//TODO figure out timing, asking questions
		this.goToLunch();
		this.goToStatusMeeting();
		this.leave();
	}

}
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
		System.out.println(clock.getFormattedClock() + "  " + name + " waits for team members to arrive");
		CountDownLatch teamStandup = this.meetings.getTeamStandUpLatch(Integer.parseInt(team)-1);
		teamStandup.countDown();
		try{
			teamStandup.await();
		} catch(InterruptedException e){
			e.printStackTrace();
		}
		
		
		//First try to acquire the conference room
		boolean roomAvailable = available.tryAcquire();
		if(roomAvailable) {
			System.out.println(clock.getFormattedClock() + "  " + name + " secures a spot in the conference room");
		} else {
			System.out.println(clock.getFormattedClock() + "  " + name + " waits for the conference room to be available");
		}
		
		//If that didn't work then wait until it can be acquired
		try {
			if(roomAvailable == false) {
				available.acquire();
				//Print out the message stating that the team lead will use the room
				System.out.println(clock.getFormattedClock() + "  " + name + " secures a spot in the conference room");
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		
		
		//Notify the team that the meeting is taking place and wait for them to arrive
		synchronized(teamStandup){
			teamStandup.notifyAll();
		}
		
		
		System.out.println(clock.getFormattedClock() + "  " + name + " hosts team standup meeting");
		timeInMeetings += 15;
		this.timeLapse(15);	
		System.out.println(clock.getFormattedClock() + "  " + name + " ends team standup meeting");
		available.release();
		
	}
	
	@Override
	public void workday(){
		this.arrive();
		this.goToManagerMeeting();
		this.goToTeamStandUpMeeting();
		
		//TODO figure out timing, asking questions
		this.goToLunch();
		int backFromLunch = clock.getClock();
		
		//4pm = 480
		this.timeLapse(480-backFromLunch);
		this.goToStatusMeeting();
		this.leave();
	}

}
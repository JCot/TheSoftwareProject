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
		System.out.println(clock.getFormattedClock() + " " + name + " knocks on the managers door.");
		
		this.managerMeetingLatch.countDown();
		try{
			this.managerMeetingLatch.await();
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
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void goToTeamStandUpMeeting(){
		System.out.println(clock.getFormattedClock() + " " + name + " waits for team members to arrive");
		teamStandUpLatch.countDown();
		try{
			teamStandUpLatch.await();
			System.out.println(clock.getFormattedClock() + " " + name + " waits for the conference room to be available");
			available.acquire();
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
		catch(InterruptedException e){}
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
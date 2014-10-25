import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;


public class TeamLead extends Worker {
	private String team;
	private String devNumber;
	private boolean isBusy = false;
	
	public TeamLead(String name, String devNumber, String teamNumber, Clock clock, CountDownLatch start, MeetingController meetings){
		super(name, clock, start, meetings);
		this.team = teamNumber;
		this.devNumber = devNumber;
		this.arrivalTime = rand.nextInt(30);
		this.lunchEndTime = rand.nextInt(480 - 240) + 240;
		this.timeAtLunch = rand.nextInt(30 - this.arrivalTime) + 30;
	}
	
	public synchronized boolean isBusy(){
		return isBusy;
	}
	
	//Try and answer a team members question
	public synchronized void answerQuestion(){
		boolean canAnswer = true;//(rand.nextInt(1) == 1);
		
		if(canAnswer){
			System.out.println(clock.getFormattedClock() + "  " + name + " answers a question");
			return;
		}
		
		else{
			System.out.println(clock.getFormattedClock() + "  " + name + " cannot answer a question. Takes question to manager");
			isBusy = true;
			askQuestion();
			isBusy = false;
			this.notifyAll();
		}
	}
	
	@Override
	public void askQuestion(){
		System.out.println(clock.getFormattedClock() + " " + name + " asks the manager a question");
	}
	
	//Go to morning meeting with PM
	public synchronized void goToManagerMeeting(){
		isBusy = true;
		CountDownLatch managerStandup = this.meetings.getManagerMeeting();
		System.out.println(clock.getFormattedClock() + "  " + name + " knocks on the manager's door.");
		
		managerStandup.countDown();
		
		int timeBeforeWaiting = clock.getClock();
		try{
			managerStandup.await();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		int timeAfterWaiting = clock.getClock();
		this.timeWorked += (timeAfterWaiting - timeBeforeWaiting);
		
		this.timeLapseWorking(15);
		
		isBusy = false;
		timeInMeetings += 15;
	}
	
	@Override
	public void goToTeamStandUpMeeting(){
		System.out.println(clock.getFormattedClock() + "  " + name + " waits for team members to arrive to work");
		CyclicBarrier teamStandup = this.meetings.getTeamStandUpLatch(Integer.parseInt(team)-1);
		int timeBefore = clock.getClock();
		try{
			teamStandup.await();
		} catch(InterruptedException e){
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		//First try to acquire the conference room
		try {
			boolean roomAvailable = available.tryAcquire();
			if(roomAvailable) {
				System.out.println(clock.getFormattedClock() + "  " + name + " secures a spot in the conference room");
			} else {
				//If that didn't work then wait until it can be acquired
				System.out.println(clock.getFormattedClock() + "  " + name + " waits for the conference room to be available");
				available.acquire();
				System.out.println(clock.getFormattedClock() + "  " + name + " secures a spot in the conference room");
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		int timeAfter = clock.getClock();
		this.timeWorked += (timeAfter - timeBefore);
		
		//Wait for employees to enter the conference room
		try{
			teamStandup.await();
		} catch(InterruptedException e){
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		
		
		System.out.println(clock.getFormattedClock() + "  " + name + " hosts team standup meeting");
		timeInMeetings += 15;
		this.timeLapseWorking(15);	
		System.out.println(clock.getFormattedClock() + "  " + name + " ends team standup meeting");
		available.release();
		
	}
	
	@Override
	public synchronized void goToLunch(){
		isBusy = true;
		super.goToLunch();
		isBusy = false;
		this.notifyAll();
	}
	
	@Override
	public void workday(){
		this.arrive();
		this.goToManagerMeeting();
		this.goToTeamStandUpMeeting();
		
		//TODO asking questions
		//Makes the employee work before going off to lunch
		synchronized(clock) {
			while (clock.getClock() < (this.lunchEndTime - this.timeAtLunch)) {
				try {
					clock.wait();
					this.timeWorked++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		this.goToLunch();
		int backFromLunch = clock.getClock();
		
		//4pm = 480
		this.timeLapseWorking(480-backFromLunch);
		//Add random time until 4:15?
		this.goToStatusMeeting();
		if(480 > timeWorked){
			this.timeLapseWorking(480 - timeWorked);
		}
		this.leave();
	}

}
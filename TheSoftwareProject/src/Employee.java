import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * 
 * @author Shannon
 *
 */
public class Employee extends Worker{
	protected String team;
	protected String devNumber;
	protected String teamLead;
	protected ArrayList<Integer> questionTimes;
	
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
		this.questionTimes = new ArrayList<Integer>();
		
		int numQuestions = rand.nextInt(1);
		for(int i = 0; i < numQuestions; i++){
			questionTimes.add(rand.nextInt(480 - 90) + 90);
		}
		
		Collections.sort(questionTimes);
	}
	
	//Ask team lead a question
	public void askQuestion(){
		System.out.println(clock.getFormattedClock() + " " + name + " asks team lead a question");
	}
	
	//Go to morning team stand-up meeting
	public void goToTeamStandUpMeeting(){
		CyclicBarrier teamStandup = this.meetings.getTeamStandUpLatch(Integer.parseInt(team)-1);
		try{
			teamStandup.await(); //Wait for all team members to arrive to work and reach this point
		}
		catch(InterruptedException e){
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		//Wait for the team lead to secure the room and for all 
		//team members to arrive to the conference room
		try{
			teamStandup.await(); 
		}
		catch(InterruptedException e){
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		timeWorked += clock.getClock() - arrivalTime;
		System.out.println(clock.getFormattedClock() + "  " + name + " goes to team standup");
		this.timeLapseWorking(15);
		timeInMeetings += 15;
	}
	
	public int getTimeWorked(){
		return timeWorked;
	}
	
	@Override
	public void workday() {
		//Start of day - employee arrives
		this.arrive();
		
		//Makes the employee wait until the manager meeting is over
		synchronized(this.meetings){
			while(!this.meetings.getManagerMeetingOver()){
				try {
					System.out.println(clock.getFormattedClock() + "  " + name + " works until team lead is finished with meeting with the manager");
					this.meetings.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		//System.err.println("before standup - " + this.name);
		this.goToTeamStandUpMeeting();
		
		//TODO asking questions
		//Makes the employee work before going off to lunch
		for(int i = 0; i < questionTimes.size(); i++){
			if(questionTimes.get(i) > this.lunchEndTime - this.timeAtLunch){
				break;
			}
			
			else{
				int askQuestionTime = questionTimes.get(i);
				
				synchronized(clock) {
					while (clock.getClock() < askQuestionTime) {
						try {
							clock.wait();
							this.timeWorked++;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				//TODO ask team lead a question. Need to figure out how to tell employee who team lead is.
			}
		}
		
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
		this.goToStatusMeeting();
		//Add random time until 4:15?
		if(480 > timeWorked){
			this.timeLapseWorking(480 - timeWorked);
		}
		this.leave();
	}
}

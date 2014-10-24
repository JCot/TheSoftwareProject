import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * MeetingController class is a class that stores latches
 * used for making workers wait to start a meeting.
 * 
 * @author Pop
 */
public class MeetingController {
	private CountDownLatch managerMeeting;
	private boolean managerMeetingOver = false;
	private CountDownLatch statusMeetingLatch = new CountDownLatch(13);
	private ArrayList<CyclicBarrier> teamStandUpLatches;
	
	public MeetingController(int numTeams, int sizeOfTeam){
		this.managerMeeting = new CountDownLatch(4);
		this.teamStandUpLatches = new ArrayList<CyclicBarrier>();
		
		for(int i = 0; i < numTeams; i++){
			this.teamStandUpLatches.add(new CyclicBarrier(sizeOfTeam));
		}
	}
	
	public synchronized void setManagerMeetingOver() {
		managerMeetingOver = true;
	}
	
	public synchronized boolean getManagerMeetingOver() {
		return managerMeetingOver;
	}
	
	public CountDownLatch getManagerMeeting(){
		return this.managerMeeting;
	}
	
	public CyclicBarrier getTeamStandUpLatch(int teamNum){
		return teamStandUpLatches.get(teamNum);
	}
	
	public CountDownLatch getStatusLatch(){
		return statusMeetingLatch;
	}
}

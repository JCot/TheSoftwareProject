import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * MeetingController class is a class that stores latches
 * used for making workers wait to start a meeting.
 * 
 * @author Pop
 */
public class MeetingController {
	private CountDownLatch managerMeeting;
	private CountDownLatch statusMeetingLatch = new CountDownLatch(13);
	private ArrayList<CountDownLatch> teamStandUpLatches;
	
	public MeetingController(int numTeams, int sizeOfTeam){
		this.managerMeeting = new CountDownLatch(4);
		this.teamStandUpLatches = new ArrayList<CountDownLatch>();
		
		for(int i = 0; i < numTeams; i++){
			this.teamStandUpLatches.add(new CountDownLatch(sizeOfTeam));
		}
	}
	
	public CountDownLatch getManagerMeeting(){
		return this.managerMeeting;
	}
	
	public CountDownLatch getTeamStandUpLatch(int teamNum){
		return teamStandUpLatches.get(teamNum);
	}
	
	public CountDownLatch getStatusLatch(){
		return statusMeetingLatch;
	}
}

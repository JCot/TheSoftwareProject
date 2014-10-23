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
	private CountDownLatch standUpLatch = new CountDownLatch(5);
	
	public MeetingController(){
		this.managerMeeting = new CountDownLatch(4);
	}
	
	public CountDownLatch getManagerMeeting(){
		return this.managerMeeting;
	}
}

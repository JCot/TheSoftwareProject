import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;


public abstract class Worker extends Thread {
	protected String name;
	protected int arrivalTime;
	protected int lunchEndTime;
	protected int timeAtLunch;
	protected Clock clock;
	protected CountDownLatch startLatch;
	protected CountDownLatch statusMeetingLatch;
	protected Random rand = new Random();
	
	protected int timeInMeetings = 0;
	
	protected static final int NUM_CONFERENCE_ROOMS = 1;
	protected static final Semaphore available = new Semaphore(NUM_CONFERENCE_ROOMS, true);
	
	/**
	 * 
	 * @param name
	 * @param clock
	 * @param startLatch
	 * @param statusMeetingLatch
	 */
	public Worker(String name, Clock clock, CountDownLatch startLatch, CountDownLatch statusMeetingLatch){
		this.name = name;
		this.clock = clock;
		this.startLatch = startLatch;
		this.statusMeetingLatch = statusMeetingLatch;
		
	}
	
	public void goToLunch(){
		System.out.println(clock.getFormattedClock() + " " + name + " goes to lunch");
	}
	
	public void arrive(){
		//Makes the employee show up to work on his or her terms
		synchronized(clock) {
			while (clock.getClock() < this.arrivalTime) {
				try {
					clock.wait(); //Should this just be wait()? So the Worker waits not the clock
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(clock.getFormattedClock() + " " + " " + name + " arrives at work");
	}
	
	public void leave(){
		System.out.println(clock.getFormattedClock() + " " + name + " leaves work");
	}
	
	//Go to the end of the day status meeting
	public void goToStatusMeeting(){
		System.out.println(clock.getFormattedClock() + " " + name + " goes to daily status meeting");
		
		this.statusMeetingLatch.countDown();
		try{
			this.statusMeetingLatch.await();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Workday is an abstract method that will simulate a worker's workday. 
	 */
	public abstract void workday();
	
	public void run(){
		try {
			this.startLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Simulate the worker's workday
		this.workday();
	}
	
	
}

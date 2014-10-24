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
	protected MeetingController meetings;
	protected Random rand = new Random();
	
	protected int timeInMeetings = 0;
	protected int timeWorked = 0;
	
	protected static final int NUM_CONFERENCE_ROOMS = 1;
	protected static final Semaphore available = new Semaphore(NUM_CONFERENCE_ROOMS, true);
	protected final int day = 4800; //milliseconds
	protected final int minute = 10; //milliseconds
	
	/**
	 * 
	 * @param name
	 * @param clock
	 * @param startLatch
	 * @param statusMeetingLatch
	 */
	public Worker(String name, Clock clock, CountDownLatch startLatch, MeetingController meetings){
		this.name = name;
		this.clock = clock;
		this.startLatch = startLatch;
		this.meetings = meetings;
	}
	
	public void goToLunch(){
		//Makes the employee work before going off to lunch
		synchronized(clock) {
			while (clock.getClock() < (this.lunchEndTime - this.timeAtLunch)) {
				try {
					clock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(clock.getFormattedClock() + "  " + name + " goes to lunch for " + this.timeAtLunch+ " minutes");
		this.timeLapse(this.timeAtLunch);
		System.out.println(clock.getFormattedClock() + "  " + name + " returns from to lunch");
	}
	
	public void arrive(){
		//Makes the employee show up to work on his or her terms
		synchronized(clock) {
			while (clock.getClock() < this.arrivalTime) {
				try {
					clock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(clock.getFormattedClock() + "  " + name + " arrives at work");
	}
	
	public void leave(){
		System.out.println(clock.getFormattedClock() + "   " + name + " leaves work");
	}
	
	//Go to the end of the day status meeting
	public void goToStatusMeeting(){
		System.out.println(clock.getFormattedClock() + "  " + name + " goes to daily status meeting");
		
		this.meetings.getStatusLatch().countDown();
		try {
			this.meetings.getStatusLatch().await();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		this.timeLapse(15);
		System.out.println(clock.getFormattedClock() + "  " + name + " leaves the daily status meeting");
		
	}
	
	protected void timeLapse(int minutes) {
		synchronized(clock){
			int time = clock.getClock();
			while(clock.getClock() < time + minutes) {
				try {
					clock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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

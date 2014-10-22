import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Clock is a shared mutable resource between all threads in the
 * software project day simulation and represents the time within the
 * day.
 * 
 * @author Andrew Popovich (ajp7560@rit.edu)
 */
public class Clock {
	
	/** Simulated minute count in which 0 represents 8am */
	private static int clock; 
	
	/** HashMap to easily calculate the hour within the simulated day */
	private static HashMap<Integer, String> actualHours;
	
	/** Formatter object for getting simulated minutes */
	private static DecimalFormat df = new DecimalFormat("00");
	
	/**
	 * Constructor for a clock object.
	 */
	public Clock() {
		actualHours = new HashMap<Integer, String>();
		
		//Stores hour and am/pm
		actualHours.put(0, "8,am");
		actualHours.put(1, "9,am");
		actualHours.put(2, "10,am");
		actualHours.put(3, "11,am");
		actualHours.put(4, "12,pm");
		actualHours.put(5, "1,pm");
		actualHours.put(6, "2,pm");
		actualHours.put(7, "3,pm");
		actualHours.put(8, "4,pm");
		actualHours.put(9, "5,pm");
	}
	
	/**
	 * Getter method for the unformatted clock. Synchronized to prevent
	 * race conditions from occurring.
	 * 
	 * @return clock - unformatted clock
	 */
	public synchronized int getClock() {
		return clock;
	}
	
	/**
	 * Increments the clock (aka number of simulated minutes) by one.
	 * Synchronized to prevent race conditions from occurring.
	 */
	public synchronized void incrementClock() {
		clock++;
		
		//Notify all objects waiting on the clock's monitor 
		//that one minute has passed
		this.notifyAll();
	}
	
	/**
	 * Returns the actual formatted time. (eg. 12:01pm)
	 * 
	 * @return String - formatted time
	 */
	public synchronized String getFormattedClock() {
		//Split the stored value from the hash map to get the hour
		//and am/pm bit separately
		String[] hourTime = actualHours.get(clock / 60).split(",");
		
		//Also, format the minutes before return
		return hourTime[0] + ":" + df.format(clock % 60) + hourTime[1];
	}
}

/**
 * Timer is a runnable object that will make the clock SMR
 * tick for each minute.
 * 
 * @author Andrew Popovich (ajp7560@rit.edu)
 *
 */
public class Timer implements Runnable {
	
	/** The amount of time (in ms) that represents a minute in the simulation */
	private int simulatedMinute;
	
	/** The amount of time (in ms) that represents a day in the simulation */
	private int simulatedDay;
	
	/** A reference to the clock object */
	private Clock clock;
	
	/**
	 * Constructor for a Timer object.
	 * 
	 * @param simulatedMinute - Parameter for the actual time used to simulate
	 *                          a minute (in ms)
	 * @param simulatedDay - Parameter for the actual time used to simulate a
	 *                       day (in ms)
	 * @param clock - Reference to the clock SMR
	 */
	public Timer(int simulatedMinute, int simulatedDay, Clock clock) {
		this.simulatedMinute = simulatedMinute;
		this.simulatedDay = simulatedDay;
		this.clock = clock;
	}
	
	/**
	 * The run method which will execute when a timer thread is ran.
	 * It will simply increment the clock for each simulated minute
	 * and will only run for the simulated day.
	 */
	@Override
	public void run() {
		
		//Determines the the amount of times to loop
		//since each iteration represents one minute of simulated time
		int runtime = simulatedDay / simulatedMinute;
		
		//Run for increments of simulatedMinutes
		//based on the simulated day
		for(int minutes = 0; minutes < runtime; minutes++){
			try {
				Thread.sleep(simulatedMinute);
				clock.incrementClock();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}

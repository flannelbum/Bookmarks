package org.eirinncraft.Bookmarks.SupportingObjects;

/**
 * Down and dirty class that simply counts milliseconds
 * <p>Timer.stop(); returns the milliseconds as a long
 * @author flannelbum
 *
 */
public class Timer {
	
	private long startTime;
	private long endTime; 
	private long totalTime;
	
	/* Constructor */
	public Timer() {
		startTime = System.currentTimeMillis();
	}
	
	public long stop() {
		endTime = System.currentTimeMillis();
		totalTime = endTime - startTime;
		return totalTime;
	}
}

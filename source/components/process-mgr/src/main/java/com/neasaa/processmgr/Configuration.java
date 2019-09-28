package com.neasaa.processmgr;

/**
This class will have all the configuration required for process manager.
All the configuration will be defaulted while creating instance of this class.
Overwrite required parameters.
*/
public class Configuration {
	
	private static final long ONE_MIN_IN_MILLIS = 1000l * 60l;
	
	/**
	 * This is default time elapse in millis since last heart beat for process to consider
	 * the process is hung.
	 */
	public static final long DEFAULT_THRESHOLD_TIME_BEFORE_PROCESS_CONSIDER_STALE =  ONE_MIN_IN_MILLIS;
	
	// Threshold time allowed before process consider stale
	private long thresholdTimeBeforeProcessConsiderStale = DEFAULT_THRESHOLD_TIME_BEFORE_PROCESS_CONSIDER_STALE;

	public long getThresholdTimeBeforeProcessConsiderStale() {
		return this.thresholdTimeBeforeProcessConsiderStale;
	}

	public void setThresholdTimeBeforeProcessConsiderStale(long aThresholdTimeBeforeProcessConsiderStale) {
		this.thresholdTimeBeforeProcessConsiderStale = aThresholdTimeBeforeProcessConsiderStale;
	}
	
}
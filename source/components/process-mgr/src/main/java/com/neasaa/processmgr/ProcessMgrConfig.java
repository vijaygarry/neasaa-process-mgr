/*
 * Copyright 2019 neasaa.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.neasaa.processmgr;

import com.neasaa.processmgr.util.GeneralUtilities;

/**
This class will have all the configuration required for process manager.
All the configuration will be defaulted while creating instance of this class.
Overwrite required parameters.
*/
public class ProcessMgrConfig {
	
	public static final long ONE_MIN_IN_MILLIS = 1000l * 60l;
	
	public static final long DEFAULT_HEART_BEAT_INTERVAL_MILLIS = ONE_MIN_IN_MILLIS;
	
	/**
	 * This is default time elapse in millis since last heart beat for process to consider
	 * the process is hung. Default value is 10 sec more than default heart beat interval
	 */
	public static final long DEFAULT_THRESHOLD_TIME_BEFORE_PROCESS_CONSIDER_STALE = DEFAULT_HEART_BEAT_INTERVAL_MILLIS + 10_000l;
	public static final String THIS_HOST_NAME = GeneralUtilities.getLocalHostName();	
	
	//Interval for heart beat.
	private long heartBeatIntervalMillis = DEFAULT_HEART_BEAT_INTERVAL_MILLIS;
	
	// Threshold time allowed before process consider stale
	private long thresholdTimeBeforeProcessConsiderStale = DEFAULT_THRESHOLD_TIME_BEFORE_PROCESS_CONSIDER_STALE;

	private String hostnameToUse = THIS_HOST_NAME;
	
	public long getThresholdTimeBeforeProcessConsiderStale() {
		return this.thresholdTimeBeforeProcessConsiderStale;
	}

	public void setThresholdTimeBeforeProcessConsiderStale(long aThresholdTimeBeforeProcessConsiderStale) {
		this.thresholdTimeBeforeProcessConsiderStale = aThresholdTimeBeforeProcessConsiderStale;
	}
	
	public long getHeartBeatIntervalMillis() {
		return this.heartBeatIntervalMillis;
	}
	
	public void setHeartBeatIntervalMillis(long aHeartBeatIntervalMillis) {
		this.heartBeatIntervalMillis = aHeartBeatIntervalMillis;
	}
	
	public void setHostnameToUse(String aHostnameToUse) {
		this.hostnameToUse = aHostnameToUse;
	}
	
	public String getHostnameToUse() {
		return this.hostnameToUse;
	}
}
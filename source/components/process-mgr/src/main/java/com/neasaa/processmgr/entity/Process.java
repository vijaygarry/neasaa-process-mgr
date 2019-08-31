package com.neasaa.processmgr.entity;

import java.util.Date;

public class Process {
	
	private long processSeqId;
	private String processName;
	private String hostname;
	private String status;
	private Date startTime;
	private Date lastHeartBeatTime;
	private Date activeStartTime;
	private Date endTime;
	private String osPid;
	private String applicationVersion;
	private String processMgrVersion;
	
	public Process() {
	}

	public long getProcessSeqId() {
		return this.processSeqId;
	}

	public void setProcessSeqId(long aProcessSeqId) {
		this.processSeqId = aProcessSeqId;
	}

	public String getProcessName() {
		return this.processName;
	}

	public void setProcessName(String aProcessName) {
		this.processName = aProcessName;
	}

	public String getHostname() {
		return this.hostname;
	}

	public void setHostname(String aHostname) {
		this.hostname = aHostname;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String aStatus) {
		this.status = aStatus;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date aStartTime) {
		this.startTime = aStartTime;
	}

	public Date getLastHeartBeatTime() {
		return this.lastHeartBeatTime;
	}

	public void setLastHeartBeatTime(Date aLastHeartBeatTime) {
		this.lastHeartBeatTime = aLastHeartBeatTime;
	}

	public Date getActiveStartTime() {
		return this.activeStartTime;
	}

	public void setActiveStartTime(Date aActiveStartTime) {
		this.activeStartTime = aActiveStartTime;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date aEndTime) {
		this.endTime = aEndTime;
	}

	public String getOsPid() {
		return this.osPid;
	}

	public void setOsPid(String aOsPid) {
		this.osPid = aOsPid;
	}

	public String getApplicationVersion() {
		return this.applicationVersion;
	}

	public void setApplicationVersion(String aApplicationVersion) {
		this.applicationVersion = aApplicationVersion;
	}

	public String getProcessMgrVersion() {
		return this.processMgrVersion;
	}

	public void setProcessMgrVersion(String aProcessMgrVersion) {
		this.processMgrVersion = aProcessMgrVersion;
	}
	
}
package com.neasaa.processmgr.entity;

import java.util.Date;

import com.neasaa.processmgr.ProcessStausEnum;

public class ProcessEntity {
	
	private long processSeqId;
	private String processName;
	private String hostname;
	private ProcessStausEnum status;
	private Date startTime;
	private Date lastHeartBeatTime;
	private long numberOfHeartBeat;
	private Date activeStartTime;
	private Date endTime;
	private String osPid;
	private String applicationVersion;
	private String processMgrVersion;
	private String notes;
	
	public ProcessEntity () {
		
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
	public ProcessStausEnum getStatus() {
		return this.status;
	}
	public void setStatus(ProcessStausEnum aStatus) {
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
	public long getNumberOfHeartBeat() {
		return this.numberOfHeartBeat;
	}
	public void setNumberOfHeartBeat(long aNumberOfHeartBeat) {
		this.numberOfHeartBeat = aNumberOfHeartBeat;
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
	public String getNotes() {
		return this.notes;
	}
	public void setNotes(String aNotes) {
		this.notes = aNotes;
	}
	
}
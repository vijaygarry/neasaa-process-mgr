package com.neasaa.processmgr;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neasaa.processmgr.dao.ProcessDAO;
import com.neasaa.processmgr.entity.ProcessEntity;
import com.neasaa.processmgr.util.GeneralUtilities;

/**
*/
public class ProcessManager {
	
	private static final Logger logger = LoggerFactory.getLogger(ProcessManager.class);
	
	public static final String THIS_HOST_NAME;
	
	static {
		THIS_HOST_NAME = GeneralUtilities.getLocalHostName();
	}
	
	private ProcessDAO processDAO;
	
	/**
		Create instance of process manager with required configurations.
	*/
	public ProcessManager (Configuration aConfiguration) {

	}

	public void setProcessDAO(ProcessDAO aProcessDAO) {
		this.processDAO = aProcessDAO;
	}
	
	/**
		Create instance of process manager with default configurations.
	*/
	public ProcessManager () {
		this (new Configuration());
	}
	
	public ProcessEntity addNewProcess (String aProcessName) {
		Date currentTime = new Date();
		ProcessEntity processEntity = new ProcessEntity();
		processEntity.setProcessName(aProcessName);
		processEntity.setHostname(THIS_HOST_NAME);
		processEntity.setStatus(ProcessStausEnum.WAITING);
		processEntity.setStartTime(currentTime);
		processEntity.setLastHeartBeatTime(currentTime);
		processEntity.setNumberOfHeartBeat(1);
		private String osPid;
		private String applicationVersion;
		private String processMgrVersion;
		
		return this.processDAO.addNewProcess(processEntity);
	}

	public ProcessEntity acquireLock (ProcessEntity aProcess) {
		ProcessEntity currentActiveProcess = this.processDAO.getCurrentActiveProcess(aProcess.getProcessName());
		
		if(currentActiveProcess == null) {
			//No active process, so acquire lock.
			
		}
		
		if(currentActiveProcess != null) {
			logger.info("Process with name " + aProcess.getProcessName() + " is actively processing on host " + currentActiveProcess.getHostname());
		}
		
		return aProcess;
	}
	
	public ProcessEntity releaseLock (ProcessEntity aProcess) {
		return aProcess;
	}
	
	public void sendHeartBeat (ProcessEntity aProcess) {
		
	}
	
	public ProcessEntity addNewProcessAndacquireLock (String aProcessName) {
		killAllStaleProcess(aProcessName);
		return null;
	}
	
	/**
	 * Kill all the process for specified process name for current host.
	 * 
	 * @param aProcessName
	 */
	public void killProcessForHost (String  aProcessName) {
		
	}
	
	
	private void killAllStaleProcess (String aProcessName) {
		
	}
	
	private boolean isProcessActiveOnOtherHost (String aProcessName) {
		return true;
	}
}
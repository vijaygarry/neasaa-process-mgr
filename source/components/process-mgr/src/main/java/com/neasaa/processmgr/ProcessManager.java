package com.neasaa.processmgr;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neasaa.processmgr.dao.ProcessDAO;
import com.neasaa.processmgr.entity.ProcessEntity;
import com.neasaa.processmgr.util.GeneralUtilities;
import com.neasaa.processmgr.util.SystemUtils;

/**
*/
public class ProcessManager {
	
	private static final Logger logger = LoggerFactory.getLogger(ProcessManager.class);
	
	public static final String THIS_HOST_NAME;
	
	static {
		THIS_HOST_NAME = GeneralUtilities.getLocalHostName();
	}
	
	private static final long ONE_MIN_IN_MILLIS = 1000l * 60l;
	private static final long HEART_BEAT_INTERVAL_MILLIS = ONE_MIN_IN_MILLIS;
	
	// 10 sec extra than heart beat interval
	private static final long DEFAULT_THRESHOLD_TIME_BEFORE_PROCESS_CONSIDER_STALE = HEART_BEAT_INTERVAL_MILLIS + 10_000l;
	
	
			
	private String processName;
	private ProcessEntity processDetails;
	private ProcessDAO processDAO;
	private HeartBeatThread heartBeatThread;
	
	/**
		Create instance of process manager with required configurations.
	*/
	public ProcessManager (String aProcessName, Configuration aConfiguration, ProcessDAO aProcessDAO) {
		this.processName = aProcessName;
		this.processDAO = aProcessDAO;
		this.processDetails = new ProcessEntity();
		Date currentTime = new Date();
		ProcessEntity processEntity = new ProcessEntity();
		processEntity.setProcessName(aProcessName);
		processEntity.setHostname(THIS_HOST_NAME);
		processEntity.setStatus(ProcessStausEnum.WAITING);
		processEntity.setStartTime(currentTime);
		processEntity.setLastHeartBeatTime(currentTime);
		processEntity.setNumberOfHeartBeat(1);
		processEntity.setOsPid(String.valueOf(SystemUtils.getCurrentJvmProcessId()));
		processEntity.setProcessMgrVersion (SystemUtils.getProcessMgrVersion());
		processEntity.setApplicationVersion("unknown");
	}
	
	public void init () {
		//Killed all stale processess
		killAllStaleProcesses();
		
		//Kill the active process with this process name if exists for this host.
		this.processDAO.killProcessForHost (this.processName, THIS_HOST_NAME, "Killed this instance to start the new instance on same host");
		
		//Insert new process in process table.
		this.processDetails = this.processDAO.addNewProcess(this.processDetails);
		
		//Start heartBeat thread
		heartBeatThread = new HeartBeatThread();
		heartBeatThread.start();
	}
	
	private void killAllStaleProcesses () {
		List<ProcessEntity> staleProcesses = this.processDAO.getStaleProcesses(DEFAULT_THRESHOLD_TIME_BEFORE_PROCESS_CONSIDER_STALE);
		if(staleProcesses != null && staleProcesses.size() > 0) {
			for(ProcessEntity process : staleProcesses) {
				logger.info("Updating process status to KILLED for process " + process);
				this.processDAO.killProcessById(process.getProcessSeqId(), "Killed this stale instance.");
			}
		}
	}
	public boolean acquireLock () {
		//get current active process for given process name
		ProcessEntity currentLockProcess = this.processDAO.getCurrentLockProcess(processName);
		
		if (currentLockProcess != null) {
			if (this.processDetails.getHostname().equals(currentLockProcess.getHostname())) {
				//We already have a lock for this process on this host.
				return true;
			}
			
			long timeSinceLastHeartBeat = System.currentTimeMillis() - currentLockProcess.getLastHeartBeatTime().getTime(); 
			if(timeSinceLastHeartBeat > DEFAULT_THRESHOLD_TIME_BEFORE_PROCESS_CONSIDER_STALE) {
				logger.info("Looks like process " + currentLockProcess + " is stale. Killing the process to get the lock.");
				this.processDAO.killProcessById(currentLockProcess.getProcessSeqId(), "Killed this stale instance to acquire lock on host: " + THIS_HOST_NAME);
			} else { 
				return false;
			}
		}
		
		// We are here means, currentLockProcess is null (No one holds the lock for this process) or 
		// Current lock is stale.
		
		//Trying to acquire lock
		
		this.processDAO.insertProcessLock(processDetails);
		this.processDAO.updateProcessToProcessing(processDetails.getProcessSeqId());
		return true;
	}
	
	
	public void releaseProcessLock () {
		this.heartBeatThread.setStopRequested(true);
		this.processDAO.releaseProcessLock(this.processDetails.getProcessSeqId());
	}
	
	
	public class HeartBeatThread extends Thread {
		private boolean stopRequested = false;
		
		public HeartBeatThread () {
		}
		
		public void setStopRequested(boolean aStopRequested) {
			this.stopRequested = aStopRequested;
		}
		
		@Override
		public void run() {
			long lastHeartBeatTime = System.currentTimeMillis();
			while(!stopRequested) {
				if((System.currentTimeMillis() - lastHeartBeatTime) > HEART_BEAT_INTERVAL_MILLIS) {
					logger.debug("Sending heartbeat for process id {}", ProcessManager.this.processDetails.getProcessSeqId());
					ProcessManager.this.processDAO.sendProcessHeartBeat(ProcessManager.this.processDetails.getProcessSeqId());
				}
				try {
					sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
//	/**
//	 * Kill all the process for specified process name for current host.
//	 * 
//	 * @param aProcessName
//	 */
//	private void killProcessForHost (String aProcessName) {
//		this.processDAO.updateProcessStatusToKill(aProcessName, THIS_HOST_NAME, "Killed this instance to start the new instance on same host");
//	}
	
//	public ProcessEntity acquireLock (ProcessEntity aProcess) {
//		ProcessEntity currentActiveProcess = this.processDAO.getCurrentActiveProcess(aProcess.getProcessName());
//		
//		if(currentActiveProcess == null) {
//			//No active process, so acquire lock.
//			
//		}
//		
//		if(currentActiveProcess != null) {
//			logger.info("Process with name " + aProcess.getProcessName() + " is actively processing on host " + currentActiveProcess.getHostname());
//		}
//		
//		return aProcess;
//	}
	
//	public ProcessEntity releaseLock (ProcessEntity aProcess) {
//		return aProcess;
//	}
//	
//	public void sendHeartBeat (ProcessEntity aProcess) {
//		
//	}
//	
//	public ProcessEntity addNewProcessAndacquireLock (String aProcessName) {
//		killAllStaleProcess(aProcessName);
//		return null;
//	}
	
//	private void killAllStaleProcess (String aProcessName) {
//		
//	}
//	
//	private boolean isProcessActiveOnOtherHost (String aProcessName) {
//		return true;
//	}
}
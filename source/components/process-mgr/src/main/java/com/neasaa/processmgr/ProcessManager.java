package com.neasaa.processmgr;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neasaa.processmgr.dao.ProcessDAO;
import com.neasaa.processmgr.entity.ProcessEntity;
import com.neasaa.processmgr.util.SystemUtils;

/**
 * Class use to manage processes across multiple instances. This class make
 * sure, once one process is active in cluster and other processes are waiting
 * for lock to start processing.
 */
public class ProcessManager {

	private static final Logger logger = LoggerFactory.getLogger(ProcessManager.class);

	private String processName;
	private ProcessEntity processDetails;
	private ProcessDAO processDAO;
	private ProcessMgrConfig configs;
	private HeartBeatThread heartBeatThread;
	private ProcessStatusChangeListner statusChangeListner;

	/**
	 * Create instance of process manager with required configurations. Make sure to
	 * call {@code ProcessManager#init(ProcessMgrConfig)} before start using this
	 * process manager.
	 */
	public ProcessManager(String aProcessName, ProcessDAO aProcessDAO,
			ProcessStatusChangeListner aStatusChangeListner) {
		this.processName = aProcessName;
		this.processDAO = aProcessDAO;
		this.statusChangeListner = aStatusChangeListner;
	}

	/**
	 * Initialize this process in database. Start the heart beat thread.
	 * 
	 * @param aProcessMgrConfig
	 */
	public void init(ProcessMgrConfig aProcessMgrConfig) {
		this.configs = aProcessMgrConfig;

		Date currentTime = new Date();
		this.processDetails = new ProcessEntity();
		this.processDetails.setProcessName(this.processName);
		this.processDetails.setHostname(this.configs.getHostnameToUse());
		this.processDetails.setStatus(ProcessStausEnum.WAITING);
		this.processDetails.setStartTime(currentTime);
		this.processDetails.setLastHeartBeatTime(currentTime);
		this.processDetails.setNumberOfHeartBeat(1);
		this.processDetails.setOsPid(String.valueOf(SystemUtils.getCurrentJvmProcessId()));
		this.processDetails.setProcessMgrVersion(SystemUtils.getProcessMgrVersion());
		this.processDetails.setApplicationVersion("unknown");

		// Killed all stale processess
		killAllStaleProcesses();

		// Kill the active process with this process name if exists for this host.
		this.processDAO.killProcessForHost(this.processName, this.configs.getHostnameToUse(),
				"Killed this instance to start the new instance on same host");

		// Insert new process in process table.
		this.processDetails = this.processDAO.addNewProcess(this.processDetails);

		// Start heartBeat thread
		heartBeatThread = new HeartBeatThread();
		heartBeatThread.start();
	}

	private void killAllStaleProcesses() {
		List<ProcessEntity> staleProcesses = this.processDAO
				.getStaleProcesses(this.configs.getThresholdTimeBeforeProcessConsiderStale());
		if (staleProcesses != null && staleProcesses.size() > 0) {
			for (ProcessEntity process : staleProcesses) {
				logger.info("Updating process status to KILLED for process " + process);
				this.processDAO.killProcessById(process.getProcessSeqId(), "Killed this stale instance.");
			}
		}
	}

	public boolean acquireLock() {
		// get current active process for given process name
		ProcessEntity currentLockProcess = this.processDAO.getCurrentLockProcess(processName);

		if (currentLockProcess != null) {
			if (this.processDetails.getHostname().equals(currentLockProcess.getHostname())) {
				// We already have a lock for this process on this host.
				return true;
			}

			long timeSinceLastHeartBeat = System.currentTimeMillis()
					- currentLockProcess.getLastHeartBeatTime().getTime();
			if (timeSinceLastHeartBeat > this.configs.getThresholdTimeBeforeProcessConsiderStale()) {
				logger.info(
						"Looks like process " + currentLockProcess + " is stale. Killing the process to get the lock.");
				this.processDAO.killProcessById(currentLockProcess.getProcessSeqId(),
						"Killed this stale instance to acquire lock on host: " + this.configs.getHostnameToUse());
			} else {
				return false;
			}
		}

		// We are here means, currentLockProcess is null (No one holds the lock for this
		// process) or
		// Current lock is stale.

		// Trying to acquire lock
		this.processDetails = this.processDAO.lockProcess(this.processDetails);
		if (this.processDetails.getStatus() == ProcessStausEnum.PROCESSING && this.statusChangeListner != null) {
			this.statusChangeListner.lockAcquired();
		}
		return true;
	}

	public boolean isLockActive() {
		// get current active process for given process name
		ProcessEntity currentLockProcess = this.processDAO.getCurrentLockProcess(processName);

		if (currentLockProcess != null) {
			if (this.processDetails.getHostname().equals(currentLockProcess.getHostname())) {
				return true;
			}
		}
		return false;
	}

	public void releaseProcessLock() {
		this.heartBeatThread.setStopRequested(true);
		this.processDAO.releaseProcessLock(this.processDetails.getProcessSeqId());
	}

	public class HeartBeatThread extends Thread {
		private boolean stopRequested = false;

		public HeartBeatThread() {
		}

		public void setStopRequested(boolean aStopRequested) {
			this.stopRequested = aStopRequested;
		}

		@Override
		public void run() {
			long lastHeartBeatTime = System.currentTimeMillis();
			while (!stopRequested) {
				if ((System.currentTimeMillis() - lastHeartBeatTime) > configs.getHeartBeatIntervalMillis()) {
					logger.debug("Sending heartbeat for process id {}",
							ProcessManager.this.processDetails.getProcessSeqId());
					try {
						ProcessEntity updatedProess = ProcessManager.this.processDAO
								.sendProcessHeartBeat(ProcessManager.this.processDetails.getProcessSeqId());
						if (ProcessManager.this.processDetails.getStatus() == ProcessStausEnum.PROCESSING
								&& updatedProess.getStatus() != ProcessStausEnum.PROCESSING) {
							if (statusChangeListner != null) {
								logger.info("Lock released for process" + updatedProess);
								statusChangeListner.lockReleased();
							}
						}
						ProcessManager.this.processDetails = updatedProess;
					} catch (Exception ex) {
						logger.error("Failed to send heartbeat for process " + ProcessManager.this.processDetails, ex);
					}
					// Even if fails to send heartbeat, wait for next heart beat time.
					lastHeartBeatTime = System.currentTimeMillis();
				}
				try {
					sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
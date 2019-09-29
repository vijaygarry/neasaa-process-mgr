package com.neasaa.processmgr;

import java.util.HashMap;
import java.util.Map;

import com.neasaa.processmgr.dao.ProcessDAO;

public class ProcessManagerFactory {
	
	private ProcessDAO processDAO;
	
	public void setProcessDAO(ProcessDAO aProcessDAO) {
		this.processDAO = aProcessDAO;
	}
	
	private Map<String, ProcessManager> processManagerMap = new HashMap<>();
	
	public ProcessManager getProcessManager(String aProcessName, ProcessStatusChangeListner aStatusChangeListner) throws Exception {
		
		if(this.processManagerMap.get(aProcessName) != null) {
			throw new Exception ("Process with name " + aProcessName + " already registered.");
		}
		
		ProcessManager processManager = new ProcessManager(aProcessName, this.processDAO, aStatusChangeListner);
		
		this.processManagerMap.put(aProcessName, processManager);
		
		return processManager;
	}
	
	public void shutdownAllProcesses () {
		
	}
}

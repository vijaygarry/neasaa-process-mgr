package com.neasaa.processmgr.dao;

import java.util.List;

import com.neasaa.processmgr.entity.ProcessEntity;

public interface ProcessDAO {
	
	ProcessEntity addNewProcess (ProcessEntity aProcess);
	
	int insertProcessLock ( ProcessEntity aProcess );
	
	ProcessEntity getCurrentLockProcess ( String aProcessName );
	
	void sendProcessHeartBeat (long aProcessId);
	
	void killProcessForHost (String aProcessName, String aHostName, String aNotes);
	
	void killProcessById (long aProcessId, String aNotes);
	
	void releaseProcessLock (long aProcessId);
	
	List<ProcessEntity> getStaleProcesses (long aThreasholdTimeInMillisToConsiderStale);
	
	void updateProcessToProcessing (long aProcessId);
}

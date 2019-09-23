package com.neasaa.processmgr.dao;

import com.neasaa.processmgr.entity.ProcessEntity;

public interface ProcessDAO {
	
	ProcessEntity addNewProcess (ProcessEntity aProcess);
	
	ProcessEntity getCurrentActiveProcess (String aProcessName);
	
	void killAllStaleProcess (String aProcessName, long aThreasholdTimeInMillisToConsiderStale);
	
	ProcessEntity insertProcessLock (ProcessEntity aProcess);
}

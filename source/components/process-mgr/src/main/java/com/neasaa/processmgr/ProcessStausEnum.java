package com.neasaa.processmgr;

public enum ProcessStausEnum {
	//Process is started and waiting to acquire lock to start processing.
	WAITING, 
	
	//Process is active and processing.
	PROCESSING,
	
	//Process exited normally and is not running.');
	EXIT_NORMAL,
	
	//This status indicate that process being forcefully killed.');
	//	-- Ideally process is killed if process is in killed status.
	//	-- Process manager update the status to killed in one of the following condition:
	//	-- 1. If process is in waiting/processing status for long without heartbeat 
	//	-- 2. Try to start the new process with same name on same host. In this case old process with waiting/processing status will be updated to killed.
	KILLED,
	
	//Error exited with some error.
	ERROR;
	
}

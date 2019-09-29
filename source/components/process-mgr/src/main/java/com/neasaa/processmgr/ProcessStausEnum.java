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

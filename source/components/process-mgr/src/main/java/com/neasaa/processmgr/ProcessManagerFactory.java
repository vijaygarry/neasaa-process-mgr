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

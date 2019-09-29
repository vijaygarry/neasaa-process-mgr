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

package com.neasaa.processmgr.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.neasaa.processmgr.ProcessStausEnum;
import com.neasaa.processmgr.entity.ProcessEntity;

public class ProcessRowMapper implements RowMapper<ProcessEntity> {
	@Override
	public ProcessEntity mapRow ( ResultSet aRs, int aRowNum )  throws SQLException {
		ProcessEntity process = new ProcessEntity ();
		process.setProcessSeqId(aRs.getLong("PROCESSSEQID"));
		process.setProcessName(aRs.getString( "PROCESSNAME") );
		process.setHostname (aRs.getString( "HOSTNAME") );
		process.setStatus (ProcessStausEnum.valueOf(aRs.getString( "STATUS")));
		process.setStartTime(JDBCUtils.getTimestampFromResult( aRs,"STARTTIME") );
		process.setLastHeartBeatTime(JDBCUtils.getTimestampFromResult( aRs, "LASTHEARTBEATTIME") );
		process.setNumberOfHeartBeat(aRs.getLong("NUMBEROFHEARTBEAT")  );
		process.setActiveStartTime(JDBCUtils.getTimestampFromResult( aRs, "ACTIVESTARTTIME") );
		process.setEndTime (JDBCUtils.getTimestampFromResult( aRs, "ENDTIME") );
		process.setOsPid(aRs.getString( "OSPID") );
		process.setApplicationVersion(aRs.getString( "APPLICATIONVERSION") );
		process.setProcessMgrVersion(aRs.getString( "PROCESSMGRVERSION") );
		process.setNotes (aRs.getString( "NOTES") );
		return process;
	}
	
	
	
}

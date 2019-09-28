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
		process.setStartTime(aRs.getDate( "STARTTIME") );
		process.setLastHeartBeatTime(aRs.getDate( "LASTHEARTBEATTIME") );
		process.setNumberOfHeartBeat(aRs.getLong("NUMBEROFHEARTBEAT")  );
		process.setActiveStartTime(aRs.getDate( "ACTIVESTARTTIME") );
		process.setEndTime (aRs.getDate( "ENDTIME") );
		process.setOsPid(aRs.getString( "OSPID") );
		process.setApplicationVersion(aRs.getString( "APPLICATIONVERSION") );
		process.setProcessMgrVersion(aRs.getString( "PROCESSMGRVERSION") );
		process.setNotes (aRs.getString( "NOTES") );
		return process;
	}
}

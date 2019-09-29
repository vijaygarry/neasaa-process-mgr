package com.neasaa.processmgr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.neasaa.processmgr.ProcessStausEnum;
import com.neasaa.processmgr.entity.ProcessEntity;

public class ProcessDAOImpl implements ProcessDAO {
	
	private static final String SCHEMA_NAME = "";
	
	public static final String KILL_STALE_PROCESS = "UPDATE " + SCHEMA_NAME + "PROCESS "
			+ " SET  STATUS = '" + ProcessStausEnum.KILLED + "', "
			+ " ENDTIME = ?, "
			+ " NOTES = ?"
			+ " WHERE PROCESSNAME = ? "
			+ " AND STATUS IN ('" + ProcessStausEnum.WAITING + "', '" + ProcessStausEnum.PROCESSING + "')"
			+ " AND LASTHEARTBEATTIME < ? ";
	
	public static final String INSERT_PROCESS_SQL = 
			  "INSERT INTO " + SCHEMA_NAME + "PROCESS "
			+ "(PROCESSNAME, HOSTNAME, STATUS, STARTTIME, LASTHEARTBEATTIME, "
			+ " NUMBEROFHEARTBEAT, ACTIVESTARTTIME, ENDTIME, OSPID, APPLICATIONVERSION, "
			+ " PROCESSMGRVERSION, NOTES) "
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public static final String UPDATE_PROCESS_TO_KILL_STATUS_SQL = 
			  "UPDATE " + SCHEMA_NAME + "PROCESS "
			+ " SET STATUS = ? , ENDTIME = ? , "
			+ " NOTES = ?  "
			+ " where PROCESSNAME = ?"
			+ " AND HOSTNAME = ?"
			+ " AND STATUS IN ('WAITING' ,'PROCESSING')";
	
	public static final String UPDATE_PROCESS_TO_KILL_STATUS_BY_ID_SQL = 
			  "UPDATE " + SCHEMA_NAME + "PROCESS "
			+ " SET STATUS = ? , ENDTIME = ? , "
			+ " NOTES = ?  "
			+ " where PROCESSSEQID = ?";
	
	public static final String UPDATE_PROCESS_TO_PROCESSING_SQL = 
			  " UPDATE " + SCHEMA_NAME + "PROCESS "
			+ " SET STATUS = 'PROCESSING',"
			+ " LASTHEARTBEATTIME = ? , NUMBEROFHEARTBEAT = NUMBEROFHEARTBEAT + 1 , "
			+ " ACTIVESTARTTIME = ? "
			+ " where PROCESSSEQID = ?";
	
	public static final String UPDATE_PROCESS_HEART_BEAT_SQL = 
			  " UPDATE PROCESS "
			+ " SET LASTHEARTBEATTIME = ? , "
			+ " NUMBEROFHEARTBEAT = NUMBEROFHEARTBEAT + 1 "
			+ " where PROCESSSEQID = ?";
	
	public static final String DELETE_PROCESS_LOCK_BY_NAME_SQL = 
			    " DELETE  PROCESSLOCK "
			  + " where PROCESSNAME = ? and HOSTNAME = ?";
	
	public static final String DELETE_PROCESS_LOCK_BY_ID_SQL = 
		    " DELETE PROCESSLOCK "
		  + " where PROCESSSEQID = ?";
	
	public static final String RELEASE_LOCK_FOR_PROCESS_SQL = 
			  " UPDATE PROCESS "
			+ " SET STATUS = 'EXIT_NORMAL' , "
			+ " LASTHEARTBEATTIME = ? , NUMBEROFHEARTBEAT = NUMBEROFHEARTBEAT + 1 , "
			+ " ENDTIME = ?"
			+ " where PROCESSSEQID = ?";
	
	public static final String SELECT_CURRENT_LOCK_PROCESS_SQL = 
			  " select  P.PROCESSSEQID , P.PROCESSNAME , P.HOSTNAME , P.STATUS , "
			+ " P.STARTTIME , P.LASTHEARTBEATTIME , P.NUMBEROFHEARTBEAT , P.ACTIVESTARTTIME , "
			+ " P.ENDTIME , P.OSPID , P.APPLICATIONVERSION , P.PROCESSMGRVERSION , P.NOTES  "
			+ " from PROCESS P, PROCESSLOCK PL "
			+ " where P.PROCESSSEQID = PL.PROCESSSEQID "
			+ " AND P.PROCESSNAME = ? ";
			//+ " AND P.STATUS = 'PROCESSING' ";
	
	public static final String SELECT_ALL_STALE_PROCESSES_SQL = 
			  " select  P.PROCESSSEQID , P.PROCESSNAME , P.HOSTNAME , P.STATUS , "
			+ " P.STARTTIME , P.LASTHEARTBEATTIME , P.NUMBEROFHEARTBEAT , P.ACTIVESTARTTIME , "
			+ " P.ENDTIME , P.OSPID , P.APPLICATIONVERSION , P.PROCESSMGRVERSION , P.NOTES  "
			+ " from PROCESS P"
			+ " where LASTHEARTBEATTIME < ?";
	
	public static final String GET_PROCESS_BY_ID_SQL = 
			  " select  P.PROCESSSEQID , P.PROCESSNAME , P.HOSTNAME , P.STATUS , "
			+ " P.STARTTIME , P.LASTHEARTBEATTIME , P.NUMBEROFHEARTBEAT , P.ACTIVESTARTTIME , "
			+ " P.ENDTIME , P.OSPID , P.APPLICATIONVERSION , P.PROCESSMGRVERSION , P.NOTES  "
			+ " from PROCESS P"
			+ " where PROCESSSEQID = ? ";
	
	public static final String INSERT_PROCESS_LOCK_SQL = 
			  " INSERT INTO PROCESSLOCK "
			+ " (PROCESSNAME, HOSTNAME, PROCESSSEQID, LOCKSTARTTIME) "
			+ " VALUES (?, ?, ?, ?)";
	
	
	
	
	
	
	
	
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate aJdbcTemplate) {
		this.jdbcTemplate = aJdbcTemplate;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}
	
	@Override
	@Transactional (transactionManager= "transactionManager", propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public ProcessEntity addNewProcess(ProcessEntity aProcess) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		getJdbcTemplate().update( new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement (aCon, aProcess);
			}
		}, keyHolder);
		
		Long processId = -1l;
		Number key = keyHolder.getKey();
		if (key != null) {
			processId = key.longValue();
		}
		aProcess.setProcessSeqId(processId);
		return aProcess;
	}
	

	public PreparedStatement buildInsertStatement(Connection aConection, ProcessEntity aProcess) throws SQLException {
		PreparedStatement prepareStatement = aConection.prepareStatement(INSERT_PROCESS_SQL, 
				new String[] { "processseqid" });
		int index = 0;
		JDBCUtils.setStringInStatement(prepareStatement, ++index, aProcess.getProcessName());
		JDBCUtils.setStringInStatement(prepareStatement, ++index, aProcess.getHostname());
		JDBCUtils.setStringInStatement(prepareStatement, ++index, aProcess.getStatus().name());
		JDBCUtils.setTimestampInStatement(prepareStatement, ++index, aProcess.getStartTime());
		JDBCUtils.setTimestampInStatement(prepareStatement, ++index, aProcess.getLastHeartBeatTime());
		JDBCUtils.setLongInStatement (prepareStatement, ++index, aProcess.getNumberOfHeartBeat());
		JDBCUtils.setTimestampInStatement(prepareStatement, ++index, aProcess.getActiveStartTime());
		JDBCUtils.setTimestampInStatement(prepareStatement, ++index, aProcess.getEndTime());
		JDBCUtils.setStringInStatement(prepareStatement, ++index, aProcess.getOsPid());
		JDBCUtils.setStringInStatement(prepareStatement, ++index, aProcess.getApplicationVersion());
		JDBCUtils.setStringInStatement(prepareStatement, ++index, aProcess.getProcessMgrVersion());
		JDBCUtils.setStringInStatement(prepareStatement, ++index, aProcess.getNotes());
		return prepareStatement;
	}

	/**
	 * This will delete the record from process lock for given process name and host name.
	 * Update process status to kill if current status is running/waiting.
	 */
	@Override
	@Transactional (transactionManager= "transactionManager", propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void killProcessForHost (String aProcessName, String aHostName, String aNotes) {
		
		deleteProcesslockByName (aProcessName, aHostName);
		
		getJdbcTemplate().update( new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				PreparedStatement prepareStatement = aConection.prepareStatement(UPDATE_PROCESS_TO_KILL_STATUS_SQL);
				JDBCUtils.setStringInStatement ( prepareStatement, 1, ProcessStausEnum.KILLED.name());
				JDBCUtils.setTimestampInStatement ( prepareStatement, 2, new Date() );
				JDBCUtils.setStringInStatement ( prepareStatement, 3, aNotes );
				JDBCUtils.setStringInStatement ( prepareStatement, 4, aProcessName );
				JDBCUtils.setStringInStatement ( prepareStatement, 5, aHostName );
				return prepareStatement;
			}
		});
	}
	
	private void deleteProcesslockByName ( String aProcessName, String aHostName ) {
		
		getJdbcTemplate().update( new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				PreparedStatement prepareStatement = aConection.prepareStatement(DELETE_PROCESS_LOCK_BY_NAME_SQL);
				JDBCUtils.setStringInStatement ( prepareStatement, 1, aProcessName );
				JDBCUtils.setStringInStatement ( prepareStatement, 2, aHostName );
				return prepareStatement;
			}
		});		
	}
	
	private void deleteProcesslockById ( long aProcessId ) {
		
		getJdbcTemplate().update( new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				PreparedStatement prepareStatement = aConection.prepareStatement(DELETE_PROCESS_LOCK_BY_ID_SQL);
				JDBCUtils.setLongInStatement (prepareStatement, 1, aProcessId);
				return prepareStatement;
			}
		});		
	}
	
	
	@Override
	@Transactional (transactionManager= "transactionManager", propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void killProcessById (long aProcessId, String aNotes) {
		
		deleteProcesslockById(aProcessId);

		getJdbcTemplate().update( new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				PreparedStatement prepareStatement = aConection.prepareStatement(UPDATE_PROCESS_TO_KILL_STATUS_BY_ID_SQL);
				JDBCUtils.setStringInStatement ( prepareStatement, 1, ProcessStausEnum.KILLED.name());
				JDBCUtils.setTimestampInStatement ( prepareStatement, 2, new Date() );
				JDBCUtils.setStringInStatement ( prepareStatement, 3, aNotes );
				JDBCUtils.setLongInStatement ( prepareStatement, 4, aProcessId );
				return prepareStatement;
			}
		});
	}
	
	@Override
	@Transactional (transactionManager= "transactionManager", propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public ProcessEntity sendProcessHeartBeat (long aProcessId) {
		getJdbcTemplate().update( new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				PreparedStatement prepareStatement = aConection.prepareStatement(UPDATE_PROCESS_HEART_BEAT_SQL);
				JDBCUtils.setTimestampInStatement ( prepareStatement, 1, new Date() );
				JDBCUtils.setLongInStatement (prepareStatement, 2, aProcessId);
				return prepareStatement;
			}
		});
		return getProcessById(aProcessId);
	}
	
	@Override
	@Transactional (transactionManager= "transactionManager", propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void releaseProcessLock (long aProcessId) {
		deleteProcesslockById(aProcessId);
		getJdbcTemplate().update( new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				PreparedStatement prepareStatement = aConection.prepareStatement(RELEASE_LOCK_FOR_PROCESS_SQL);
				Date currDate = new Date();
				JDBCUtils.setTimestampInStatement ( prepareStatement, 1, currDate );
				JDBCUtils.setTimestampInStatement ( prepareStatement, 2, currDate );
				JDBCUtils.setLongInStatement (prepareStatement, 3, aProcessId);				
				return prepareStatement;
			}
		});
	}
	
	@Override
	@Transactional (transactionManager= "transactionManager", propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public ProcessEntity getCurrentLockProcess ( String aProcessName ) {
		List<ProcessEntity> processes = getJdbcTemplate().query(SELECT_CURRENT_LOCK_PROCESS_SQL, new ProcessRowMapper(), aProcessName);
		if(processes != null && processes.size() > 0) {
			return processes.get(0);
		}
		return null;
	}
	
	
	private int insertProcessLock ( ProcessEntity aProcess ) {
		return getJdbcTemplate().update( new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				PreparedStatement prepareStatement = aConection.prepareStatement(INSERT_PROCESS_LOCK_SQL);
				JDBCUtils.setStringInStatement ( prepareStatement, 1, aProcess.getProcessName() );
				JDBCUtils.setStringInStatement ( prepareStatement, 2, aProcess.getHostname () );
				JDBCUtils.setLongInStatement ( prepareStatement, 3, aProcess.getProcessSeqId() );
				JDBCUtils.setTimestampInStatement ( prepareStatement, 4, new Date() );
				return prepareStatement;
			}
		});
	}
	
	@Override
	@Transactional (transactionManager= "transactionManager", propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public List<ProcessEntity> getStaleProcesses (long aThreasholdTimeInMillisToConsiderStale) {
		Date threasholdDate = new Date(System.currentTimeMillis() - aThreasholdTimeInMillisToConsiderStale);
		return getJdbcTemplate().query(SELECT_ALL_STALE_PROCESSES_SQL, new ProcessRowMapper(), threasholdDate);
	}

	@Override
	@Transactional (transactionManager= "transactionManager", propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public ProcessEntity lockProcess(ProcessEntity aProcess) {
		insertProcessLock ( aProcess );
		updateProcessToProcessing (aProcess.getProcessSeqId());
		return getProcessById(aProcess.getProcessSeqId());
	}
	
	private void updateProcessToProcessing(long aProcessId) {
		getJdbcTemplate().update( new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				PreparedStatement prepareStatement = aConection.prepareStatement(UPDATE_PROCESS_TO_PROCESSING_SQL);
				Date currDate = new Date();
				JDBCUtils.setTimestampInStatement ( prepareStatement, 1, currDate );
				JDBCUtils.setTimestampInStatement ( prepareStatement, 2, currDate );
				JDBCUtils.setLongInStatement (prepareStatement, 3, aProcessId);				
				return prepareStatement;
			}
		});		
	}

	private ProcessEntity getProcessById (long aProcessId) {
		List<ProcessEntity> processes = getJdbcTemplate().query(GET_PROCESS_BY_ID_SQL, new ProcessRowMapper(), aProcessId);
		if(processes != null && processes.size() > 0) {
			return processes.get(0);
		}
		return null;
	}	

}

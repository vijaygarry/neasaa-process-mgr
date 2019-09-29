package com.neasaa.processmgr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.neasaa.processmgr.ProcessStausEnum;
import com.neasaa.processmgr.entity.ProcessEntity;

public class ProcessDAOImpl implements ProcessDAO {

	/**
	 * UTC time zone constant.
	 */
	public static final String UTC_TIME_ZONE = "UTC";
	
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
	
	/**
	 * This method check the specified string value. If specified string is null, then set null value for specified index
	 * else sets specified string at specified index.
	 * 
	 * @param aPreparedStatement
	 * @param aIndex
	 * @param aStringValue
	 * @throws SQLException
	 */
	public final static void setStringInStatement ( PreparedStatement aPreparedStatement, int aIndex,
			String aStringValue ) throws SQLException {
		if(aStringValue == null) {
			aPreparedStatement.setNull(aIndex, Types.VARCHAR);
		} else {
			aPreparedStatement.setString( aIndex, aStringValue );
		}
	}
	
	
	/**
	 * Returns the Sql Timestamp object for specified java.util.Date object.
	 * 
	 * @param aDate
	 * @return sql.Timestamp object for specified date.
	 */
	public static Timestamp dateToSqlTimestamp (Date aDate) {
		if(aDate == null) {
			return null;
		}
		return new Timestamp (aDate.getTime());
	}
	
	/**
	 * Returns a new Calendar instance whose time zone is set to UTC.
	 *
	 * @return a new Calendar instance whose time zone is set to UTC.
	 */
	public static Calendar getUtcCalendarInstance () {
		return Calendar.getInstance( TimeZone.getTimeZone( UTC_TIME_ZONE ) );
	}
	
	public final static void setTimestampInStatement ( PreparedStatement aPreparedStatement, int aIndex,
			Date aDateValue ) throws SQLException {
		if(aDateValue == null) {
			aPreparedStatement.setNull(aIndex, Types.TIMESTAMP);
		} else {
			aPreparedStatement.setTimestamp(aIndex, dateToSqlTimestamp( aDateValue ),
					getUtcCalendarInstance());
		}
	}
	
	public final static void setLongInStatement ( PreparedStatement aPreparedStatement, int aIndex, long aLongValue )
			throws SQLException {
		aPreparedStatement.setLong( aIndex, aLongValue );
	}
	
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
		setStringInStatement(prepareStatement, ++index, aProcess.getProcessName());
		setStringInStatement(prepareStatement, ++index, aProcess.getHostname());
		setStringInStatement(prepareStatement, ++index, aProcess.getStatus().name());
		setTimestampInStatement(prepareStatement, ++index, aProcess.getStartTime());
		setTimestampInStatement(prepareStatement, ++index, aProcess.getLastHeartBeatTime());
		setLongInStatement (prepareStatement, ++index, aProcess.getNumberOfHeartBeat());
		setTimestampInStatement(prepareStatement, ++index, aProcess.getActiveStartTime());
		setTimestampInStatement(prepareStatement, ++index, aProcess.getEndTime());
		setStringInStatement(prepareStatement, ++index, aProcess.getOsPid());
		setStringInStatement(prepareStatement, ++index, aProcess.getApplicationVersion());
		setStringInStatement(prepareStatement, ++index, aProcess.getProcessMgrVersion());
		setStringInStatement(prepareStatement, ++index, aProcess.getNotes());
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
				setStringInStatement ( prepareStatement, 1, ProcessStausEnum.KILLED.name());
				setTimestampInStatement ( prepareStatement, 2, new Date() );
				setStringInStatement ( prepareStatement, 3, aNotes );
				setStringInStatement ( prepareStatement, 4, aProcessName );
				setStringInStatement ( prepareStatement, 5, aHostName );
				return prepareStatement;
			}
		});
	}
	
	private void deleteProcesslockByName ( String aProcessName, String aHostName ) {
		
		getJdbcTemplate().update( new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				PreparedStatement prepareStatement = aConection.prepareStatement(DELETE_PROCESS_LOCK_BY_NAME_SQL);
				setStringInStatement ( prepareStatement, 1, aProcessName );
				setStringInStatement ( prepareStatement, 2, aHostName );
				return prepareStatement;
			}
		});		
	}
	
	private void deleteProcesslockById ( long aProcessId ) {
		
		getJdbcTemplate().update( new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				PreparedStatement prepareStatement = aConection.prepareStatement(DELETE_PROCESS_LOCK_BY_ID_SQL);
				setLongInStatement (prepareStatement, 1, aProcessId);
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
				setStringInStatement ( prepareStatement, 1, ProcessStausEnum.KILLED.name());
				setTimestampInStatement ( prepareStatement, 2, new Date() );
				setStringInStatement ( prepareStatement, 3, aNotes );
				setLongInStatement ( prepareStatement, 4, aProcessId );
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
				setTimestampInStatement ( prepareStatement, 1, new Date() );
				setLongInStatement (prepareStatement, 2, aProcessId);
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
				setTimestampInStatement ( prepareStatement, 1, currDate );
				setTimestampInStatement ( prepareStatement, 2, currDate );
				setLongInStatement (prepareStatement, 3, aProcessId);				
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
				setStringInStatement ( prepareStatement, 1, aProcess.getProcessName() );
				setStringInStatement ( prepareStatement, 2, aProcess.getHostname () );
				setLongInStatement ( prepareStatement, 3, aProcess.getProcessSeqId() );
				setTimestampInStatement ( prepareStatement, 4, new Date() );
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
				setTimestampInStatement ( prepareStatement, 1, currDate );
				setTimestampInStatement ( prepareStatement, 2, currDate );
				setLongInStatement (prepareStatement, 3, aProcessId);				
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

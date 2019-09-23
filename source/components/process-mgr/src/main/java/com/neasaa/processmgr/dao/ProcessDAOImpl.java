package com.neasaa.processmgr.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.neasaa.processmgr.ProcessStausEnum;
import com.neasaa.processmgr.entity.ProcessEntity;
import com.neasaa.processmgr.util.GeneralUtilities;

public class ProcessDAOImpl implements ProcessDAO {

	/**
	 * UTC time zone constant.
	 */
	public static final String UTC_TIME_ZONE = "UTC";
	
	public static final String KILL_STALE_PROCESS = "UPDATE SAIX_AUTH.PROCESS "
			+ " SET  STATUS = '" + ProcessStausEnum.KILLED + "', "
			+ " ENDTIME = ?, "
			+ " NOTES = ?"
			+ " WHERE PROCESSNAME = ? "
			+ " AND STATUS IN ('" + ProcessStausEnum.WAITING + "', '" + ProcessStausEnum.PROCESSING + "')"
			+ " AND LASTHEARTBEATTIME < ? ";
	
	public static final String INSERT_PROCESS_LOCK_SQL = 
			  " INSERT INTO PROCESSLOCK "
			+ " (PROCESSNAME, HOSTNAME, PROCESSSEQID)" 
			+ " VALUES (?, ?, ?) ";
	
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
	
	@Override
	public ProcessEntity addNewProcess(ProcessEntity aProcess) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Updated all the process with process name as specified process name and 
	 */
	@Override
	public void killAllStaleProcess(String aProcessName, long aThreasholdTimeInMillisToConsiderStale) {
		PreparedStatement prepareStatement = null;
		//End time
		setTimestampInStatement(prepareStatement, 1, new Date());
		 
		 //Notes
		setStringInStatement (prepareStatement, 2, "Updated to killed by host " + GeneralUtilities.getLocalHostName() + " because of inactivity for more than " + aThreasholdTimeInMillisToConsiderStale + " millis");
		setStringInStatement (prepareStatement, 3, aProcessName);
		 
		 long threasholdTime = System.currentTimeMillis() - aThreasholdTimeInMillisToConsiderStale;
		 // Threshold time
		 setTimestampInStatement(prepareStatement, 4, new Date(threasholdTime));		
		
	}


	@Override
	public ProcessEntity getCurrentActiveProcess(String aProcessName) {
//		SELECT PROCESSSEQID, PROCESSNAME, HOSTNAME, STATUS, STARTTIME, LASTHEARTBEATTIME,
//		NUMBEROFHEARTBEAT, ACTIVESTARTTIME, ENDTIME, OSPID, APPLICATIONVERSION,
//		PROCESSMGRVERSION, NOTES
//		FROM PROCESSLOCK pl, PROCESS p
//		WHERE pl.PROCESSSEQID = p.PROCESSSEQID
//		AND p.PROCESSNAME = ?
		return null;
	}


	@Override
	public ProcessEntity insertProcessLock(ProcessEntity aProcess) {
//		INSERT_PROCESS_LOCK_SQL
		
		return null;
	}
	

}

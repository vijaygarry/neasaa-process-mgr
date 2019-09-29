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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class JDBCUtils {

	/**
	 * UTC time zone constant.
	 */
	public static final String UTC_TIME_ZONE = "UTC";
	
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
	
	public static Date getTimestampFromResult (ResultSet aRs, String aColumnName) throws SQLException {
		return aRs.getTimestamp( aColumnName, getUtcCalendarInstance());
	}
}

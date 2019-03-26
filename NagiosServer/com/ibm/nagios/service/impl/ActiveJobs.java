package com.ibm.nagios.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.CommonUtil;
import com.ibm.nagios.util.JDBCConnection;
import com.ibm.nagios.util.StatusConstants;

public class ActiveJobs implements Action {
	public ActiveJobs(){
	}

	public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
		int actJobNum = 0;
		Statement stmt = null;
		ResultSet rs = null;
		int returnValue = StatusConstants.UNKNOWN;
		
		String warningCap = args.get("-W");
		String criticalCap = args.get("-C");
		int intWarningCap = (warningCap == null) ? 100 : Integer.parseInt(warningCap);
		int intCriticalCap = (criticalCap == null) ? 100 : Integer.parseInt(criticalCap);
		Connection connection = null;
		try {
			JDBCConnection JDBCConn = new JDBCConnection();
			connection = JDBCConn.getJDBCConnection(as400.getSystemName(), args.get("-U"), args.get("-P"), args.get("-SSL"));
			if(connection == null) {
				response.append(StatusConstants.retrieveDataError + "| " + "Cannot get the JDBC connection");
				return returnValue;
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT ACTIVE_JOBS_IN_SYSTEM FROM QSYS2.SYSTEM_STATUS_INFO");
			if(rs == null) {
				response.append(StatusConstants.retrieveDataError + "| " + "Cannot retrieve data from server");
				return returnValue;
			}

			while(rs.next()) {
				actJobNum=rs.getInt("ACTIVE_JOBS_IN_SYSTEM");		
			}

			returnValue = CommonUtil.getStatus(actJobNum, intWarningCap, intCriticalCap, returnValue);
			response.insert(0, "Num of Active Jobs: " + actJobNum + " | 'Num of Active Jobs' = " + actJobNum + ";" + warningCap + ";" + criticalCap);
		}
		catch(Exception e) {
			response.append(StatusConstants.retrieveDataException + "| " + e.getMessage());
			CommonUtil.printStack(e.getStackTrace(), response);
			e.printStackTrace();
		}
		finally {
			try {
				if(rs != null)
					rs.close();
				if(stmt != null)
					stmt.close();
				if(connection != null)
					connection.close();
			} catch (SQLException e) {
				response.append(StatusConstants.retrieveDataException + "| " + e.getMessage());
				e.printStackTrace();
			}
		}
		return returnValue;
	}
}

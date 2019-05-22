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
import com.ibm.nagios.util.Constants;

public class PageFaults implements Action {
	public PageFaults(){
	}

	public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
		String poolName = null;
		double totalFaults = 0;
		double maxFaults = 0;
		Statement stmt = null;
		ResultSet rs = null;
		String warningCap = args.get("-W");
		String criticalCap = args.get("-C");
		double doubleWarningCap = (warningCap == null) ? 100 : Double.parseDouble(warningCap);
		double doubleCriticalCap = (criticalCap == null) ? 100 : Double.parseDouble(criticalCap);
		int returnValue = Constants.UNKNOWN;
		
		Connection connection = null;
		try {
			JDBCConnection JDBCConn = new JDBCConnection();
			connection = JDBCConn.getJDBCConnection(as400.getSystemName(), args.get("-U"), args.get("-P"), args.get("-SSL"));
			if(connection == null) {
				response.append(Constants.retrieveDataError + "| " + "Cannot get the JDBC connection");
				return returnValue;
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT POOL_NAME, ELAPSED_TOTAL_FAULTS FROM QSYS2.POOL_INFO");
			if(rs == null) {
				response.append(Constants.retrieveDataError + "| " + "Cannot retrieve data from server");
				return returnValue;
			}
			while(rs.next()) {
				poolName = rs.getString("POOL_NAME");
				totalFaults = rs.getDouble("ELAPSED_TOTAL_FAULTS");
				
				maxFaults = totalFaults>maxFaults ? totalFaults : maxFaults;
				returnValue = CommonUtil.getStatus(totalFaults, doubleWarningCap, doubleCriticalCap, returnValue);
				response.append("'" + poolName + "'=" + totalFaults + ";" + doubleWarningCap + ";" + doubleCriticalCap);
			}
			response.insert(0, "Highest Page Faults: " + maxFaults + " | ");
		}
		catch(Exception e) {
			response.setLength(0);
			response.append(Constants.retrieveDataException + "| " + e.getMessage());
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
				response.append(Constants.retrieveDataException + "| " + e.getMessage()==null ? e.toString() : e.getMessage());
				e.printStackTrace();
			}
		}
		return returnValue;
	}
}

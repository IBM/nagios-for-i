package com.ibm.nagios.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.CommonUtil;
import com.ibm.nagios.util.JDBCConnection;
import com.ibm.nagios.util.StatusConstants;

public class IASPUsage implements Action {
	DecimalFormat usageFormat = new DecimalFormat("0.00%");
	
	public IASPUsage(){
	}

	@SuppressWarnings("unused")
	public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
		double percentUsed = 0.;
		String description = null;
		int aspNum;
		String type = null;
		String state = null;
		long totalCapacity;
		long capacityAvailable;
		double maxIASPUsgVal = 0;
		int IASPNum = 0;
		Statement stmt = null;
		ResultSet rs = null;
		String warningCap = args.get("-W");
		String criticalCap = args.get("-C");
		double doubleWarningCap = (warningCap == null) ? 100 : Double.parseDouble(warningCap);
		double doubleCriticalCap = (criticalCap == null) ? 100 : Double.parseDouble(criticalCap);
		int returnValue = StatusConstants.UNKNOWN;
		
		Connection connection = null;
		try {
			JDBCConnection JDBCConn = new JDBCConnection();
			connection = JDBCConn.getJDBCConnection(as400.getSystemName(), args.get("-U"), args.get("-P"), args.get("-SSL"));
			if(connection == null) {
				response.append(StatusConstants.retrieveDataError + "| " + "Cannot get the JDBC connection");
				return returnValue;
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT DEVICE_DESCRIPTION_NAME, ASP_NUMBER, ASP_STATE, ASP_TYPE, TOTAL_CAPACITY, TOTAL_CAPACITY_AVAILABLE FROM QSYS2.ASP_INFO");
			if(rs == null) {
				response.append(StatusConstants.retrieveDataError + "| " + "Cannot retrieve data from server");
				return returnValue;
			}
			while(rs.next()) {
				aspNum = rs.getInt("ASP_NUMBER");
				if(aspNum > 32) {
					IASPNum++;
					description = rs.getString("DEVICE_DESCRIPTION_NAME");
					state = rs.getString("ASP_STATE");
					type = rs.getString("ASP_TYPE");
					totalCapacity = rs.getLong("TOTAL_CAPACITY");
					capacityAvailable = rs.getLong("TOTAL_CAPACITY_AVAILABLE");
					percentUsed = (double)capacityAvailable/(double)totalCapacity;
					
					maxIASPUsgVal = percentUsed>maxIASPUsgVal ? percentUsed : maxIASPUsgVal;
					returnValue = CommonUtil.getStatus(percentUsed, doubleWarningCap, doubleCriticalCap, returnValue);
					response.append("'IASP " + aspNum + "'= " + usageFormat.format(percentUsed/100) + ";" + doubleWarningCap + ";" + doubleCriticalCap);
				}
			}
			if(IASPNum == 0) {
				response.insert(0, "IASP not found");
			} else {
				response.insert(0, "Highest IASP Usage: " + usageFormat.format(maxIASPUsgVal/100) + " | ");
			}

			return returnValue;
		}
		catch(Exception e) {
			response.setLength(0);
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


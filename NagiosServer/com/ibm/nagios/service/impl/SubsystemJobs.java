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

public class SubsystemJobs implements Action {	
	public SubsystemJobs(){
	}

	public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
		int returnValue = Constants.UNKNOWN;
		
		String warningCap = args.get("-W");
		String criticalCap = args.get("-C");
		int intWarningCap = (warningCap == null) ? 100 : Integer.parseInt(warningCap);
		int intCriticalCap = (criticalCap == null) ? 100 : Integer.parseInt(criticalCap);
		
		String subsystem = args.get("-S");
		if(subsystem == null) {
			response.append("The argument -S [subsystem] is not set");
			return returnValue;
		}
		String jobName = null;
		String curentUser = null;
		String CPUPercent = null;
		String jobStatus = null;
		String functionType = null;
		String function = null;
		String jobType = null;
		int jobCount = 0;
		Statement stmt = null;
		ResultSet rs = null;
		
		Connection connection = null;
		try {
			JDBCConnection JDBCConn = new JDBCConnection();
			connection = JDBCConn.getJDBCConnection(as400.getSystemName(), args.get("-U"), args.get("-P"), args.get("-SSL"));
			if(connection == null) {
				response.append(Constants.retrieveDataError + "| " + "Cannot get the JDBC connection");
				return returnValue;
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT SUBSTR(JOB_NAME,8,POSSTR(SUBSTR(JOB_NAME,8),'/')-1) AS JOB_USER, SUBSTR(SUBSTR(JOB_NAME,8),POSSTR(SUBSTR(JOB_NAME,8),'/')+1)  AS JOB_NAME, AUTHORIZATION_NAME, ELAPSED_CPU_PERCENTAGE, FUNCTION_TYPE, FUNCTION, JOB_STATUS, JOB_TYPE FROM TABLE (QSYS2.ACTIVE_JOB_INFO('NO', '', '', '')) AS X WHERE SUBSYSTEM = '" + subsystem.toUpperCase() + "'");
			if(rs == null) {
				response.append(Constants.retrieveDataError + "| " + "Cannot retrieve data from server");
				return returnValue;
			}		
			while(rs.next()) {
				function = rs.getString("FUNCTION");
//				if(function == null)
//					continue;
				jobName = String.format("%-10s", rs.getString("JOB_NAME"));
				curentUser = String.format("%-10s", rs.getString("JOB_USER"));
				CPUPercent = String.format("%-5s", rs.getString("ELAPSED_CPU_PERCENTAGE"));
				jobStatus = String.format("%-4s", rs.getString("JOB_STATUS"));
				functionType = rs.getString("FUNCTION_TYPE");
				jobType = rs.getString("JOB_TYPE");
				function = functionType + "-" + function;
				jobCount++;
				if(!jobType.equalsIgnoreCase("SBS")) {
					response.append("Job: " + jobName + " CPU: " + CPUPercent + " User: " + curentUser + " Status: " + jobStatus + " Function: " + function + "\n");
				}
			}
			if(jobCount == 0) {
				response.insert(0, "subsystem " + subsystem + " is not active\n");
				returnValue = Constants.CRITICAL;
			} else {
				response.insert(0, jobCount-1 + " jobs in subsystem " + subsystem + "\n");
				returnValue = CommonUtil.getStatus(jobCount, intWarningCap, intCriticalCap, returnValue);
			}
		}
		catch(Exception e) {
			response.setLength(0);
			response.append(Constants.retrieveDataException + "| " + e.getMessage()==null ? e.toString() : e.getMessage());
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
				response.append(Constants.retrieveDataException + "| " + e.getMessage());
			}
		}
		return returnValue;
	}
}

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
import com.ibm.nagios.util.Constants;

public class CPUOverloadJobs implements Action {
	DecimalFormat df = new DecimalFormat("######0.00");
	
	public CPUOverloadJobs() {
		
	}
	
	public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
		double CPUPercentage = 0.;
		int jobCount = 0;
		String user = null;
		String jobName = null;
		String subsystem = null;
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
				response.append(Constants.retrieveDataError + " - " + "Cannot get the JDBC connection");
				return returnValue;
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT SUBSTR(JOB_NAME,8,POSSTR(SUBSTR(JOB_NAME,8),'/')-1) AS JOB_USER, SUBSTR(SUBSTR(JOB_NAME,8),POSSTR(SUBSTR(JOB_NAME,8),'/')+1) AS JOB_NAME, ELAPSED_CPU_PERCENTAGE, SUBSYSTEM FROM TABLE(QSYS2.ACTIVE_JOB_INFO('NO', '', '', '')) AS X");
			if(rs == null) {
				response.append(Constants.retrieveDataError + " - " + "Cannot retrieve data from server");
				return returnValue;
			}		
			while(rs.next()) {
				CPUPercentage = rs.getDouble("ELAPSED_CPU_PERCENTAGE");
				
				returnValue = CommonUtil.getStatus(CPUPercentage, doubleWarningCap, doubleCriticalCap, returnValue);
				if(returnValue != Constants.OK) {
					user = rs.getString("JOB_USER");
					jobName = String.format("%-10s", rs.getString("JOB_NAME"));
					subsystem = rs.getString("SUBSYSTEM");
					subsystem = subsystem!=null ? subsystem : "-   ";
					response.append("Job: " + jobName + " USER: " + user + " SUBSYSTEM: " + subsystem + "CPU: " + df.format(CPUPercentage) + "%\n");
					jobCount++;
				}
			}
			response.insert(0, "CPU overload job num: " + jobCount + " | 'CPU overload job num' = " + jobCount +"\n");
			return returnValue;
		}
		catch(Exception e) {
			response.append(Constants.retrieveDataException + " - " + e.toString());
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
				response.append(Constants.retrieveDataException + " - " + e.toString());
				e.printStackTrace();
			}
		}
		return returnValue;
	}
}

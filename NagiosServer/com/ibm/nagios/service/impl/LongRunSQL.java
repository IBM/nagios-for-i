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

public class LongRunSQL implements Action {
	DecimalFormat df = new DecimalFormat("######0.00");
	
	public LongRunSQL(){
	}

	public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
		double duration = 0.;
		int warnCount = 0;
	    int criticalCount = 0;
		String stmtText = null;
		Statement stmt = null;
		ResultSet rs = null;
		String warningCap = args.get("-W");
		String criticalCap = args.get("-C");
		double doubleWarningCap = (warningCap == null) ? 10 : Double.parseDouble(warningCap);	// turn to millisecond
		double doubleCriticalCap = (criticalCap == null) ? 6 : Double.parseDouble(criticalCap);	// turn to millisecond
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
			rs = stmt.executeQuery("WITH ACTIVE_USER_JOBS (Q_JOB_NAME, CPU_TIME, RUN_PRIORITY) AS (" +
					"SELECT JOB_NAME, CPU_TIME, RUN_PRIORITY FROM TABLE (QSYS2.ACTIVE_JOB_INFO('NO','','','')) x WHERE JOB_TYPE <> 'SYS'" +
					") SELECT Q_JOB_NAME, CPU_TIME, RUN_PRIORITY, V_SQL_STATEMENT_TEXT, CURRENT TIMESTAMP - V_SQL_STMT_START_TIMESTAMP AS SQL_STMT_DURATION, B.* FROM ACTIVE_USER_JOBS, TABLE(QSYS2.GET_JOB_INFO(Q_JOB_NAME)) B " +
					"WHERE V_SQL_STMT_STATUS = 'ACTIVE' ORDER BY SQL_STMT_DURATION DESC");
			if(rs == null) {
				response.append(Constants.retrieveDataError + "| " + "Cannot retrieve data from server");
				return returnValue;
			}
			while(rs.next()) {
				duration = rs.getDouble("SQL_STMT_DURATION");
				stmtText = rs.getString("V_SQL_STATEMENT_TEXT");
				
				returnValue = CommonUtil.getStatus(duration, doubleWarningCap, doubleCriticalCap, returnValue);
				if(returnValue == Constants.CRITICAL) {
					criticalCount++;
					response.append("CRITICAL: Statement Duration=" + df.format(duration) + "s\n Stmt Text: " + stmtText);
				}
				if(returnValue == Constants.WARN) {
					warnCount++;
					response.append("WARNING: Statement Duration=" + df.format(duration) + "s\n Stmt Text: " + stmtText);
				}
			}
			if (returnValue == Constants.WARN) {
		        response.insert(0, "Long Run SQL Status: Warning Num: " + warnCount + " (Warning: " + doubleWarningCap + " Critical: " + doubleCriticalCap + ")\n");
			}
			else if (returnValue == Constants.CRITICAL) {
				response.insert(0, "Long Run SQL Status: Critical Num: " + criticalCount + " (Warning: " + doubleWarningCap + " Critical: " + doubleCriticalCap + ")\n");
			}
			else if (returnValue == Constants.OK) {
				response.insert(0, "Long Run SQL Status: OK\n");
			}
		}
		catch(Exception e) {
			String errMsg = e.getMessage();
			if(errMsg.contains("[SQL0443] NOT AUTHORIZED")) {
				errMsg = "Service long run sql needs authority of *ALLOBJ";
			}
			response.append(Constants.retrieveDataException + "| " + errMsg);
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

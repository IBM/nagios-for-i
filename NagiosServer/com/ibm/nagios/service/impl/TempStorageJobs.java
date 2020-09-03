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

public class TempStorageJobs implements Action {
    public TempStorageJobs() {
    }

    public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
        try {
            int version = as400.getVersion();
            int release = as400.getRelease();
            if (version < 7 || (version >= 7 && release == 1)) {
                response.append(Constants.retrieveDataError + " - " + "The service is not supported on this release");
                return Constants.UNKNOWN;
            }
        } catch (Exception eVR) {
            response.append(Constants.retrieveDataException + " - " + eVR.getMessage());
            return Constants.UNKNOWN;
        }
        String jobCount = args.get("-N");
        int jobNum = 0;
        jobCount = jobCount == null ? "10" : jobCount;
        String jobName = null;
        String currentUser = null;
        String CPUPercent = null;
        String storageConsumed = null;
        String maxStorage = null;
        String jobStatus = null;
        String subsystem = null;
        String functionType = null;
        String function = null;
        Statement stmt = null;
        ResultSet rs = null;
        int returnValue = Constants.UNKNOWN;

        Connection connection = null;
        try {
            JDBCConnection JDBCConn = new JDBCConnection();
            connection = JDBCConn.getJDBCConnection(as400.getSystemName(), args.get("-U"), args.get("-P"), args.get("-SSL"));
            if (connection == null) {
                response.append(Constants.retrieveDataError + " - " + "Cannot get the JDBC connection");
                return returnValue;
            }
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT SUBSTR(JOB_NAME,8,POSSTR(SUBSTR(JOB_NAME,8),'/')-1) AS JOB_USER, SUBSTR(SUBSTR(JOB_NAME,8),POSSTR(SUBSTR(JOB_NAME,8),'/')+1) AS JOB_NAME, SUBSYSTEM, ELAPSED_CPU_PERCENTAGE, FUNCTION_TYPE, FUNCTION, JOB_STATUS, TEMPORARY_STORAGE FROM TABLE (QSYS2.ACTIVE_JOB_INFO('NO', '', '', '')) X " +
                    "ORDER BY TEMPORARY_STORAGE DESC FETCH FIRST " + jobCount + " ROWS ONLY");
            if (rs == null) {
                response.append(Constants.retrieveDataError + " - " + "Cannot retrieve data from server");
                return returnValue;
            }
            while (rs.next()) {
                jobName = String.format("%-10s", rs.getString("JOB_NAME"));
                currentUser = String.format("%-10s", rs.getString("JOB_USER"));
                CPUPercent = rs.getString("ELAPSED_CPU_PERCENTAGE");
                storageConsumed = String.format("%-4s", rs.getString("TEMPORARY_STORAGE"));
                if (maxStorage == null) {
                    maxStorage = storageConsumed;
                }
                jobStatus = String.format("%-4s", rs.getString("JOB_STATUS"));
                subsystem = rs.getString("SUBSYSTEM");
                subsystem = String.format("%-10s", subsystem != null ? subsystem : "-  ");
                functionType = rs.getString("FUNCTION_TYPE");
                function = rs.getString("FUNCTION");
                if (functionType == null && function == null) {
                    function = "-   ";
                } else {
                    function = functionType + "-" + function;
                }
                response.append("Job: " + jobName + " Storage: " + storageConsumed + "M CPU: " + CPUPercent + " User: " + currentUser + " Status: " + jobStatus + " Subsystem: " + subsystem + " Function: " + function + "\n");
                jobNum++;
            }
            response.insert(0, jobNum + " jobs retrieved from endpoint. Max storage=" + maxStorage + "M\n");
            returnValue = Constants.OK;
        } catch (Exception e) {
            response.setLength(0);
            response.append(Constants.retrieveDataException + " - " + e.toString());
            CommonUtil.printStack(e.getStackTrace(), response);
            CommonUtil.logError(args.get("-H"), this.getClass().getName(), e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                response.append(Constants.retrieveDataException + " - " + e.toString());
            }
        }
        return returnValue;
    }
}

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

public class CPU implements Action {
    public CPU() {
    }

    public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
        double AverageCPU = 0.;
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
            if (connection == null) {
                response.append(Constants.retrieveDataError + " - " + "Cannot get the JDBC connection");
                return returnValue;
            }
            stmt = connection.createStatement();
            // CPU info is now in SYSTEM_ACTIVITY_INFO
            rs = stmt.executeQuery("SELECT AVERAGE_CPU_UTILIZATION FROM TABLE(QSYS2.SYSTEM_ACTIVITY_INFO())");
            if (rs == null) {
                response.append(Constants.retrieveDataError + " - " + "Cannot retrieve data from server");
                return returnValue;
            }
            if (rs.next()) {
                AverageCPU = rs.getDouble("AVERAGE_CPU_UTILIZATION");

                returnValue = CommonUtil.getStatus(AverageCPU, doubleWarningCap, doubleCriticalCap, returnValue);
                response.append("CPU Utilization = " + AverageCPU + "%" + " | 'CPU Utilization' = " + AverageCPU + "%;" + doubleWarningCap + ";" + doubleCriticalCap);
                return returnValue;
            }
        } catch (Exception e) {
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
                e.printStackTrace();
            }
        }
        return returnValue;
    }
}

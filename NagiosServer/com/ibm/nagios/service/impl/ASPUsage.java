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

public class ASPUsage implements Action {
    DecimalFormat usageFormat = new DecimalFormat("0.00%");

    public ASPUsage() {
    }

    @SuppressWarnings("unused")
    public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
        double percentUsed = 0.;
        long ASPStorage;
        long curTempStorage;
        double maxASPUsgVal = 0;
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
            rs = stmt.executeQuery("SELECT SYSTEM_ASP_USED, SYSTEM_ASP_STORAGE, CURRENT_TEMPORARY_STORAGE FROM QSYS2.SYSTEM_STATUS_INFO");
            if (rs == null) {
                response.append(Constants.retrieveDataError + " - " + "Cannot retrieve data from server");
                return returnValue;
            }
            while (rs.next()) {
                percentUsed = rs.getDouble("SYSTEM_ASP_USED");
                ASPStorage = rs.getLong("SYSTEM_ASP_STORAGE");
                curTempStorage = rs.getLong("CURRENT_TEMPORARY_STORAGE");
                maxASPUsgVal = percentUsed > maxASPUsgVal ? percentUsed : maxASPUsgVal;

                returnValue = CommonUtil.getStatus(percentUsed, doubleWarningCap, doubleCriticalCap, returnValue);
                response.append("'ASP Usage' = " + usageFormat.format(percentUsed / 100) + ";" + doubleWarningCap + ";" + doubleCriticalCap);
            }
            response.insert(0, "Highest ASP Usage: " + usageFormat.format(maxASPUsgVal / 100) + " | ");
            return returnValue;
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
                e.printStackTrace();
            }
        }
        return returnValue;
    }
}


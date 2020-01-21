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

public class DiskUsage implements Action {
    DecimalFormat usageFormat = new DecimalFormat("0.00%");

    public DiskUsage() {
    }

    @SuppressWarnings("unused")
    public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
        double percentUsed = 0.;
        String aspNumber = null;
        String unitNumber = null;
        String unitType = null;
        long capacity;
        long available;
        double maxDskUsgVal = 0;
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
            rs = stmt.executeQuery("SELECT ASP_NUMBER, UNIT_NUMBER, UNIT_TYPE, UNIT_STORAGE_CAPACITY, UNIT_SPACE_AVAILABLE, PERCENT_USED FROM QSYS2.SYSDISKSTAT");
            if (rs == null) {
                response.append(Constants.retrieveDataError + " - " + "Cannot retrieve data from server");
                return returnValue;
            }
            while (rs.next()) {
                percentUsed = rs.getDouble("PERCENT_USED");
                aspNumber = rs.getString("ASP_NUMBER");
                unitNumber = rs.getString("UNIT_NUMBER");
                unitType = rs.getString("UNIT_TYPE");
                capacity = rs.getLong("UNIT_STORAGE_CAPACITY") / 1000000;
                available = rs.getLong("UNIT_SPACE_AVAILABLE") / 1000000;

                maxDskUsgVal = percentUsed > maxDskUsgVal ? percentUsed : maxDskUsgVal;
                returnValue = CommonUtil.getStatus(percentUsed, doubleWarningCap, doubleCriticalCap, returnValue);
                response.append("'Unit " + aspNumber + "-" + unitNumber + "'= " + usageFormat.format(percentUsed / 100) + ";" + doubleWarningCap + ";" + doubleCriticalCap);
            }
            response.insert(0, "Highest Disk Usage:" + usageFormat.format(maxDskUsgVal / 100) + " | ");
            return returnValue;
        } catch (Exception e) {
            response.setLength(0);
            response.append(Constants.retrieveDataException + " - " + e.toString());
            CommonUtil.printStack(e.getStackTrace(), response);
            e.printStackTrace();
        } finally {
            usageFormat = null;
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

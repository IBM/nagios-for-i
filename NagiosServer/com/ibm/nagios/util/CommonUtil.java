package com.ibm.nagios.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {
	private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
    public static int getStatus(int value, int warningCap, int CriticalCap, int curStatus) {
        //int retVal = StatusConstants.UNKNOWN;
        if (value >= CriticalCap) {
            curStatus = Constants.CRITICAL;
        } else if (value >= warningCap && curStatus != Constants.CRITICAL) {
            curStatus = Constants.WARN;
        } else if (value < warningCap && curStatus != Constants.CRITICAL && curStatus != Constants.WARN) {
            curStatus = Constants.OK;
        }
        return curStatus;
    }

    public static int getStatus(double value, double warningCap, double CriticalCap, int curStatus) {
        //int retVal = StatusConstants.UNKNOWN;
        if (value >= CriticalCap) {
            curStatus = Constants.CRITICAL;
        } else if (value >= warningCap && curStatus != Constants.CRITICAL) {
            curStatus = Constants.WARN;
        } else if (value < warningCap && curStatus != Constants.CRITICAL && curStatus != Constants.WARN) {
            curStatus = Constants.OK;
        }
        return curStatus;
    }
    
    public static int getStatus(long value, long warningCap, long CriticalCap, int curStatus) {
        //int retVal = StatusConstants.UNKNOWN;
        if (value >= CriticalCap) {
            curStatus = Constants.CRITICAL;
        } else if (value >= warningCap && curStatus != Constants.CRITICAL) {
            curStatus = Constants.WARN;
        } else if (value < warningCap && curStatus != Constants.CRITICAL && curStatus != Constants.WARN) {
            curStatus = Constants.OK;
        }
        return curStatus;
    }

    public static void printStack(StackTraceElement[] elements, StringBuffer response) {
        for (int i = 0; i < elements.length; i++) {
            response.append('\n');
            response.append(elements[i]);
        }
    }
    
    synchronized public static void logError(String system, String plugin, String errMsg) {
    	StringBuffer buf = new StringBuffer();
    	buf.append(format.format(new Date())).append('\t').append(system==null ? "Unknown Host" : system).append('\t').append(plugin).append(": ").append(errMsg);
    	System.out.println(buf.toString());
    }
    
}

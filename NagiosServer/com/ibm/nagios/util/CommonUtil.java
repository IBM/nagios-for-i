package com.ibm.nagios.util;

public class CommonUtil {

	public static int getStatus(int value, int warningCap, int CriticalCap, int curStatus) {
		//int retVal = StatusConstants.UNKNOWN;
		if(value >= CriticalCap) {
			curStatus = Constants.CRITICAL;
		}
		else if(value >= warningCap && curStatus!= Constants.CRITICAL) {
			curStatus = Constants.WARN;
		}
		else if(value < warningCap && curStatus!= Constants.CRITICAL && curStatus!= Constants.WARN) {
			curStatus = Constants.OK;
		}
		return curStatus;
	}
	
	public static int getStatus(double value, double warningCap, double CriticalCap, int curStatus) {
		//int retVal = StatusConstants.UNKNOWN;
		if(value >= CriticalCap) {
			curStatus = Constants.CRITICAL;
		}
		else if(value >= warningCap && curStatus!= Constants.CRITICAL) {
			curStatus = Constants.WARN;
		}
		else if(value < warningCap && curStatus!= Constants.CRITICAL && curStatus!= Constants.WARN) {
			curStatus = Constants.OK;
		}
		return curStatus;
	}
	
	public static void printStack(StackTraceElement[] elements, StringBuffer response) {
		for(int i=0; i<elements.length; i++) {
			response.append('\n');
			response.append(elements[i]);
		}
	}
}

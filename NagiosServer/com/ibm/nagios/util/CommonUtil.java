package com.ibm.nagios.util;

public class CommonUtil {

	public static int getStatus(int value, int warningCap, int CriticalCap, int curStatus) {
		//int retVal = StatusConstants.UNKNOWN;
		if(value >= CriticalCap) {
			curStatus = StatusConstants.CRITICAL;
		}
		else if(value >= warningCap && curStatus!= StatusConstants.CRITICAL) {
			curStatus = StatusConstants.WARN;
		}
		else if(value < warningCap && curStatus!= StatusConstants.CRITICAL && curStatus!= StatusConstants.WARN) {
			curStatus = StatusConstants.OK;
		}
		return curStatus;
	}
	
	public static int getStatus(double value, double warningCap, double CriticalCap, int curStatus) {
		//int retVal = StatusConstants.UNKNOWN;
		if(value >= CriticalCap) {
			curStatus = StatusConstants.CRITICAL;
		}
		else if(value >= warningCap && curStatus!= StatusConstants.CRITICAL) {
			curStatus = StatusConstants.WARN;
		}
		else if(value < warningCap && curStatus!= StatusConstants.CRITICAL && curStatus!= StatusConstants.WARN) {
			curStatus = StatusConstants.OK;
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

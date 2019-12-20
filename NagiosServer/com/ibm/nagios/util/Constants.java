package com.ibm.nagios.util;

public interface Constants {
	public static final String SERVER = "localhost";
	public static final int PORT = 8888;
	
	public static String retrieveDataException = "Exception";
    public static String retrieveDataError = "Error";
    public static int OK = 0;
    public static int WARN = 1;
    public static int CRITICAL = 2;
    public static int UNKNOWN = 3;
    public static String[] STATUS = {"OK", "WARN", "CRITICAL", "UNKNOWN"};
}

package com.ibm.nagios.util;

public class CustomBean {
	public String commonName = null;
	public String type = null;
	public String sqlCmd = null;
	
	public CustomBean(String commonName, String type, String sqlCmd) {
		this.commonName = commonName;
		this.type = type;
		this.sqlCmd = sqlCmd;
	}
}

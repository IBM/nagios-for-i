package com.ibm.nagios.util;

import java.util.ArrayList;

public class CustomBean {
	public String commonName = null;
	public String type = null;
	public String sqlCmd = null;
	public String warning = null;
	public String critical = null;
	public ArrayList<CustomCommand> preCmd = null;
	public ArrayList<CustomCommand> postCmd = null;
	
	public CustomBean(String commonName, String type, String sqlCmd, String warning, String critical, ArrayList<CustomCommand> preCmd, ArrayList<CustomCommand> postCmd) {
		this.commonName = commonName;
		this.type = type;
		this.sqlCmd = sqlCmd;
		this.warning = warning;
		this.critical = critical;
		this.preCmd = preCmd;
		this.postCmd = postCmd;
	}
}

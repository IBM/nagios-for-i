package com.ibm.nagios.util;

public class CustomCommand {
	String command;
	String type;
	
	public CustomCommand(String command, String type) {
		this.command = command;
		this.type = type;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
}

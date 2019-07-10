package com.ibm.nagios.util;

import com.ibm.nagios.service.Action;

public class ActionFactory {
	
	public static Action get(String action) throws Exception {
		String classDir = "com.ibm.nagios.service.impl";
		String className = classDir + "." + action;

		Action actionInstance = null;
		actionInstance = (Action)Class.forName(className).newInstance();
		return actionInstance;
	}
}

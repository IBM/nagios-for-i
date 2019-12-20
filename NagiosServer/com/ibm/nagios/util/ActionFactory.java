package com.ibm.nagios.util;

import com.ibm.nagios.service.Action;

public class ActionFactory {
	
	public static Action get(String action) throws Exception {
		String classDir = "com.ibm.nagios.service.impl";
		String className = classDir + "." + action;

		Action actionInstance = null;
		try {
			actionInstance = (Action)Class.forName(className).newInstance();
		} catch(ClassNotFoundException e) {
			className = "com.ibm.nagios.hmc.service.Impl" + "." + action;
			actionInstance = (Action)Class.forName(className).newInstance();
		}
		return actionInstance;
	}
}

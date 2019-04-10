package com.ibm.nagios.util;

import com.ibm.nagios.service.Action;

public class ActionFactory {
	
	public static Action get(String action) {
		String classDir = "com.ibm.nagios.service.impl";
		String className = classDir + "." + action;

		Action actionInstance = null;
		try {
			actionInstance = (Action)Class.forName(className).newInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return actionInstance;
	}
}

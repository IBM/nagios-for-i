package com.ibm.nagios.util;

import java.util.concurrent.ConcurrentHashMap;

import com.ibm.nagios.service.Action;

public class ActionFactory {
	ConcurrentHashMap<String, Action> cachedActions = new ConcurrentHashMap<String, Action>();
	public ActionFactory() {
		
	}
	
	public Action get(String action) {
		Action actionInstance = cachedActions.get(action);
		if(actionInstance == null) {
			String classDir = "com.ibm.nagios.service.impl";
			String className = classDir + "." + action;
	
			try {
				actionInstance = (Action)Class.forName(className).newInstance();
				//cachedActions.put(action, actionInstance);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return actionInstance;
	}
}

package com.ibm.nagios.service;

import java.util.Map;

import com.ibm.as400.access.AS400;

public interface Action {   
	public int execute(AS400 as400, Map<String, String> args, StringBuffer response);
}

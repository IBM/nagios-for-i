package com.ibm.nagios.util;

import java.util.concurrent.ConcurrentHashMap;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400ConnectionPool;

public class AS400Connection {
	private static ConcurrentHashMap<String, AS400ConnectionPool> as400Pool = new ConcurrentHashMap<String, AS400ConnectionPool>();
	
	public AS400 getAS400Object(String systemName, String user, String password, StringBuffer response, String ssl) {
		try {
			AS400ConnectionPool pool = as400Pool.get(systemName+user);
			if(pool == null) {
				pool = new AS400ConnectionPool();
				pool.setMaxConnections(14);
				pool.setMaxUseTime(10000);
				as400Pool.put(systemName+user, pool);
			}
			AS400 as400 = null;
			if(ssl!=null && ssl.equalsIgnoreCase("Y")) {
				as400 = pool.getSecureConnection(systemName, user, password, AS400.COMMAND);
			} else {
				as400 = pool.getConnection(systemName, user, password, AS400.COMMAND);
			}
			return as400;
		} catch (Exception e) {
			response.append("AS400Connection - getAS400Object(): " + e.getMessage() + "\n");
			CommonUtil.printStack(e.getStackTrace(), response);
			e.printStackTrace();
		}
		return null;
	}
	
	synchronized public static void ReturnConnectionToPool(String key, AS400 as400) {
		as400Pool.get(key).returnConnectionToPool(as400);
	}
}

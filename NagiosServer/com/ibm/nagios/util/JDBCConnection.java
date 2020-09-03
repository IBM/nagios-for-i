package com.ibm.nagios.util;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.as400.access.AS400JDBCConnectionPool;
import com.ibm.as400.access.AS400JDBCConnectionPoolDataSource;
import com.ibm.as400.access.ConnectionPoolException;

public class JDBCConnection {
    private static ConcurrentHashMap<String, AS400JDBCConnectionPool> JDBCPool = new ConcurrentHashMap<String, AS400JDBCConnectionPool>();
    private static ConcurrentHashMap<String, AS400JDBCConnectionPool> SecureJDBCPool = new ConcurrentHashMap<String, AS400JDBCConnectionPool>();
    
    static {
    	//init the timer to reset the JDBC connection pool
    	//to resolve issue caused by ERROR SQL0519 prepared statement in use
    	jdbcConnPoolResetTimer();
    }
    
    public synchronized Connection getJDBCConnection(String system, String userID, String password, String ssl) throws ConnectionPoolException {
        AS400JDBCConnectionPool pool = null;

        if (ssl != null && ssl.equalsIgnoreCase("Y")) {
        	synchronized(JDBCPool) {
        		pool = SecureJDBCPool.get(system);
        	
	            if (pool == null) {
	                AS400JDBCConnectionPoolDataSource dataSource = new AS400JDBCConnectionPoolDataSource(system, userID, password);
	                dataSource.setSecure(true);
	                dataSource.setSocketTimeout(120000);////(60000 millisec == 1 min)- required  when pool is trying to connect to a system that was shutdown after pool was opened.. in that case without timeout connection will hang
	                dataSource.setThreadUsed(false);
	                pool = new AS400JDBCConnectionPool(dataSource);
	                pool.setMaxConnections(100);
	                SecureJDBCPool.put(system, pool);
	            }
        	}
        } else {
        	synchronized(SecureJDBCPool) {
        		pool = JDBCPool.get(system);
        	
	            if (pool == null) {
	                AS400JDBCConnectionPoolDataSource dataSource = new AS400JDBCConnectionPoolDataSource(system, userID, password);
	                dataSource.setSocketTimeout(120000);
	                dataSource.setThreadUsed(false);
	                pool = new AS400JDBCConnectionPool(dataSource);
	                pool.setMaxConnections(100);
	                JDBCPool.put(system, pool);
	            }
        	}
        }
        Connection conn = pool.getConnection();
        return conn;
    }
    
    public static void jdbcConnPoolResetTimer() {
    	Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();
  
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
            	synchronized(JDBCPool) {
            		for(Map.Entry<String, AS400JDBCConnectionPool> entry : JDBCPool.entrySet()) {
            			entry.getValue().close();
            		}
            		JDBCPool.clear();
            		System.out.println("JDBC pool cleared at " + new Date().toString());
            	}
            	synchronized(SecureJDBCPool) {
            		for(Map.Entry<String, AS400JDBCConnectionPool> entry : SecureJDBCPool.entrySet()) {
            			entry.getValue().close();
            		}
            		SecureJDBCPool.clear();
            		System.out.println("Secure JDBC pool cleared at " + new Date().toString());
            	}
            }
        }, time, 1000 * 60 * 60 * 24);
        System.out.println("JDBC connection pool reset timer started");
    } 
}

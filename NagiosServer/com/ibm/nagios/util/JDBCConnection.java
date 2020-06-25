package com.ibm.nagios.util;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.as400.access.AS400JDBCConnectionPool;
import com.ibm.as400.access.AS400JDBCConnectionPoolDataSource;
import com.ibm.as400.access.ConnectionPoolException;

public class JDBCConnection {
    private static ConcurrentHashMap<String, AS400JDBCConnectionPool> JDBCPool = new ConcurrentHashMap<String, AS400JDBCConnectionPool>();
    private static ConcurrentHashMap<String, AS400JDBCConnectionPool> SecureJDBCPool = new ConcurrentHashMap<String, AS400JDBCConnectionPool>();

    public Connection getJDBCConnection(String system, String userID, String password, String ssl) throws ConnectionPoolException {
        AS400JDBCConnectionPool pool = null;

        if (ssl != null && ssl.equalsIgnoreCase("Y")) {
            pool = SecureJDBCPool.get(system);
            if (pool == null) {
                AS400JDBCConnectionPoolDataSource dataSource = new AS400JDBCConnectionPoolDataSource(system, userID, password);
                dataSource.setSecure(true);
                dataSource.setSocketTimeout(120000);////(60000 millisec == 1 min)- required  when pool is trying to connect to a system that was shutdown after pool was opened.. in that case without timeout connection will hang
                dataSource.setThreadUsed(false);
                pool = new AS400JDBCConnectionPool(dataSource);
                pool.setMaxConnections(30);
                SecureJDBCPool.put(system, pool);
            }
        } else {
            pool = JDBCPool.get(system);
            if (pool == null) {
                AS400JDBCConnectionPoolDataSource dataSource = new AS400JDBCConnectionPoolDataSource(system, userID, password);
                dataSource.setSocketTimeout(120000);
                dataSource.setThreadUsed(false);
                pool = new AS400JDBCConnectionPool(dataSource);
                pool.setMaxConnections(30);
                JDBCPool.put(system, pool);
            }
        }
        Connection conn = pool.getConnection();
        return conn;
    }
}

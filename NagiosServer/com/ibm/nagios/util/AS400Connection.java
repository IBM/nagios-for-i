package com.ibm.nagios.util;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400ConnectionPool;
import com.ibm.as400.access.ConnectionPoolException;

public class AS400Connection {
    private static AS400ConnectionPool as400Pool = new AS400ConnectionPool();

    static {
        as400Pool.setMaxLifetime(1000 * 60 * 30);
    }

    public AS400 getAS400Object(String systemName, String user, String password, StringBuffer response, String ssl) throws ConnectionPoolException {
        AS400 as400 = null;
        if (ssl != null && ssl.equalsIgnoreCase("Y")) {
            as400 = as400Pool.getSecureConnection(systemName, user, password, AS400.COMMAND);
        } else {
            as400 = as400Pool.getConnection(systemName, user, password, AS400.COMMAND);
        }
        return as400;
    }

    synchronized public static void ReturnConnectionToPool(String key, AS400 as400) {
        as400Pool.returnConnectionToPool(as400);
    }
}

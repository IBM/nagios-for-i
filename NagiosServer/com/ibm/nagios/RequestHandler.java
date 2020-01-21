package com.ibm.nagios;

import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.AS400Connection;
import com.ibm.nagios.util.ActionFactory;
import com.ibm.nagios.util.Constants;

public class RequestHandler {
    public RequestHandler() {
    }

    synchronized public int process(AS400 as400, Map<String, String> args, StringBuffer response) throws Exception {
        return CollectStatus(as400, args, response);
    }

    private int CollectStatus(AS400 as400, Map<String, String> args, StringBuffer response) throws Exception {
        int retVal = 3; //UNKNOW = 3
        String metric = args.get("-M");
        if (metric == null) {
            response.append("The argument -m is not set");
            return Constants.UNKNOWN;
        }
        String systemName = args.get("-H");
        String user = args.get("-U");
        Action action = ActionFactory.get(metric);
        retVal = action.execute(as400, args, response);
        if (as400 != null) {
            // TODO maybe need a better way to generate the key
            AS400Connection.ReturnConnectionToPool(systemName + user, as400);
        }
        return retVal;
    }
}

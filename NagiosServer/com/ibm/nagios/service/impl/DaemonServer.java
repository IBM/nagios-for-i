package com.ibm.nagios.service.impl;

import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.Constants;

public class DaemonServer implements Action {
    public DaemonServer() {
    }

    public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
        response.append("Daemon server: OK");
        return Constants.OK;
    }
}

package com.ibm.nagios;

import java.util.Map;

import com.ibm.as400.access.AS400;

public class CheckIBMiStatus {
    AS400 as400;
    Map<String, String> args;

    public CheckIBMiStatus(AS400 as400, Map<String, String> args) {
        this.as400 = as400;
        this.args = args;
    }

    public int run(StringBuffer response) throws Exception {
        RequestHandler handler = new RequestHandler();
        return handler.process(as400, args, response);
    }
}

package com.ibm.nagios.hmc.service.Impl;

import java.io.InputStream;
import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.nagios.hmc.HMCConstants;
import com.ibm.nagios.hmc.HMCInfo;
import com.ibm.nagios.hmc.HMCSessionFactory;
import com.ibm.nagios.hmc.HTTPRequestUtilities;
import com.ibm.nagios.hmc.xml.XmlParser;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.CommonUtil;
import com.ibm.nagios.util.Constants;

public class ManagementConsoleVersion implements Action {
    public static final String API = "/rest/api/uom/ManagementConsole";

    public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
        int returnValue = Constants.OK;

        // used for certificate verify
        String ksPass = args.get("-KSPASS");
        String port = args.get("-PORT");
        String system = args.get("-H");
        String user = args.get("-U");
        String password = args.get("-P");

        HMCInfo hmcInfo = new HMCInfo(system, port, ksPass, user, password);
        try {
            HMC.preCheck(hmcInfo, ksPass); //if the check failed, it will throw exception to terminate the process

            //used for rest api

            String sURL = HMCConstants.HTTPS + system + HMCConstants.PORT + API;
            InputStream response_is = HTTPRequestUtilities.sendGetRequest(sURL, null, HMCSessionFactory.getSession(hmcInfo));

            XmlParser xmlparser = new XmlParser(response_is);

            String consoleVersion = xmlparser.getNodeValue("BaseVersion");
            response.append("Management Console Version: " + consoleVersion);
        } catch (Exception e) {
            response.append(Constants.retrieveDataException + " -  " + e.toString());
            CommonUtil.printStack(e.getStackTrace(), response);
            returnValue = Constants.UNKNOWN;
            e.printStackTrace();
        }
        return returnValue;
    }

}

package com.ibm.nagios.hmc.service.Impl;

import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.nagios.config.util.Base64Coder;
import com.ibm.nagios.hmc.HMCConstants;
import com.ibm.nagios.hmc.HMCInfo;
import com.ibm.nagios.hmc.cert.InstallCertificate;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.ActionFactory;
import com.ibm.nagios.util.CommonUtil;
import com.ibm.nagios.util.Constants;
import com.ibm.nagios.util.HostConfigInfo;

public class HMC implements Action {
    public static final String PORT = ":" + HMCConstants.HMC_PORT;
    public static final String HTTPS = "https://";

    public HMC() {

    }

    public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
        int returnValue = Constants.UNKNOWN;
        try {
	        String func = args.get("-F");
	        if (func == null) {
	            response.append("The argument -F [function name] is not set");
	            return returnValue;
	        }
	
	        String system = args.get("-H");
	        String user = HostConfigInfo.getHMCUserID(system);
	        if (user == null) {
	            response.append("HMC user profile not set");
	            return returnValue;
	        }
	        String password = Base64Coder.decodeString(HostConfigInfo.getHMCPassword(system));
	        args.put("-U", user);
	        args.put("-P", password);
        
            Action action = ActionFactory.get(func);
            returnValue = action.execute(null, args, response);
            return returnValue;
        } catch (Exception e) {
            //TODO Pre-defined class not found. Load the plugin from CustomHMC.xml
            response.append(Constants.retrieveDataException + " -  " + e.toString());
            CommonUtil.printStack(e.getStackTrace(), response);
            e.printStackTrace();
        }
        return returnValue;
    }

    public static void preCheck(HMCInfo hmcInfo, String ksPass) throws Exception {
        if (InstallCertificate.preCheckStatus.get(hmcInfo.getSystem()) == null) {
            if (!InstallCertificate.isCertificateInstalled(hmcInfo)) {
                InstallCertificate.install_all(hmcInfo, ksPass);
            } else {
                InstallCertificate.preCheckStatus.put(hmcInfo.getSystem(), "OK");
            }
        }
    }
}

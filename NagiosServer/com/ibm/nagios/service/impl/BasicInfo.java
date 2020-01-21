package com.ibm.nagios.service.impl;

import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.CommonUtil;
import com.ibm.nagios.util.Constants;

public class BasicInfo implements Action {
    public BasicInfo() {
    }

    public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
        int returnValue = Constants.OK;

        try {
            int VRM = as400.getVRM();
            int M = VRM & 255;                     //low 8 bits
            int R = (VRM & (255 << 8)) >> 8;     //middle 8 bits
            int V = (VRM & (65535 << 16)) >> 16; //high 16 bits
            StringBuilder vrmInfo = new StringBuilder();
            vrmInfo.append('V');
            vrmInfo.append(V);
            vrmInfo.append('R');
            vrmInfo.append(R);
            vrmInfo.append('M');
            vrmInfo.append(M);
            response.append("System Info: " + vrmInfo.toString());
            int expireDays = as400.getPasswordExpirationDays();
            if (0 < expireDays && expireDays < 7) {
                returnValue = Constants.WARN;
                response.setLength(0);
                response.append("Password will be expired in " + expireDays + " days");
            }
        } catch (Exception e) {
            response.append(Constants.retrieveDataException + " - " + e.toString());
            CommonUtil.printStack(e.getStackTrace(), response);
            returnValue = Constants.UNKNOWN;
            e.printStackTrace();
        }
        return returnValue;
    }
}

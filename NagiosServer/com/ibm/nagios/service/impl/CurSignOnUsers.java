package com.ibm.nagios.service.impl;

import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.BinaryConverter;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.CommonUtil;
import com.ibm.nagios.util.QWCRSSTS;
import com.ibm.nagios.util.Constants;

public class CurSignOnUsers implements Action {
    public CurSignOnUsers() {

    }

    public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
        int ccsid = as400.getCcsid();
        AS400Text text4 = new AS400Text(4, ccsid, as400);
        byte[] data = null;
        int returnValue = Constants.UNKNOWN;

        String warningCap = args.get("-W");
        String criticalCap = args.get("-C");
        int intWarningCap = (warningCap == null) ? 100 : Integer.parseInt(warningCap);
        int intCriticalCap = (criticalCap == null) ? 100 : Integer.parseInt(criticalCap);

        QWCRSSTS pgmCall = new QWCRSSTS();
        try {
            data = pgmCall.run(as400, response);
            if (data == null) {
                response.append(Constants.retrieveDataError);
                return returnValue;
            }
            int signOnUserNum = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, 24)), 0);

            returnValue = CommonUtil.getStatus(signOnUserNum, intWarningCap, intCriticalCap, returnValue);
            response.insert(0, "Currently Sign On Users Num: " + signOnUserNum + " | 'Currently Sign On Users Num' = " + signOnUserNum + ";" + warningCap + ";" + criticalCap);
        } catch (Exception e) {
            response.append(Constants.retrieveDataException + " - " + e.toString());
            CommonUtil.printStack(e.getStackTrace(), response);
            e.printStackTrace();
        } finally {
            data = null;
        }
        return returnValue;
    }

}

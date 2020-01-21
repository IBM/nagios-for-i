package com.ibm.nagios.hmc.service.Impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.ibm.as400.access.AS400;
import com.ibm.nagios.hmc.HMCConstants;
import com.ibm.nagios.hmc.HMCInfo;
import com.ibm.nagios.hmc.HMCSessionFactory;
import com.ibm.nagios.hmc.HTTPRequestUtilities;
import com.ibm.nagios.hmc.Partition;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.CommonUtil;
import com.ibm.nagios.util.Constants;

public class SpecificPartition implements Action {
    public static final String API = "/rest/api/uom/LogicalPartition/quick/All";

    @Override
    public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
        int returnValue = Constants.UNKNOWN;

        // used for certificate verify
        String ksPass = args.get("-KSPASS");
        String port = args.get("-PORT");
        String hmcIpAddr = args.get("-HMCIP");		//HMC IP address
        String user = args.get("-U");				//HMC user
        String password = args.get("-P");			//HMC password
        String fieldOfInterest = args.get("-FOI");	//field of interest
        String partitionName = args.get("-PARNAME");//partition name of current system
        String expectValue = args.get("-EXPVAL");	//expect values, split with ','
        String warningCap = args.get("-W");
        String criticalCap = args.get("-C");
        long longWarningCap = (warningCap == null) ? 100 : Long.valueOf(warningCap);
        long longCriticalCap = (criticalCap == null) ? 100 : Long.valueOf(criticalCap);

        HMCInfo hmcInfo = new HMCInfo(hmcIpAddr, port, ksPass, user, password);
        try {
            HMC.preCheck(hmcInfo, ksPass); //if the check failed, it will throw exception to terminate the process

            //used for rest api
            String sURL = HMCConstants.HTTPS + hmcIpAddr + HMCConstants.PORT + API;
            InputStream response_is = HTTPRequestUtilities.sendGetRequest(sURL, null, HMCSessionFactory.getSession(hmcInfo));

            List<Partition> list = new ArrayList<Partition>();
            InputStreamReader rio = null;
            Type listType = new TypeToken<List<Partition>>() {
            }.getType();

            rio = new InputStreamReader(response_is);

            Gson gson = new Gson();
            JsonReader jr = gson.newJsonReader(rio);

            list = gson.fromJson(jr, listType);
            for (Partition partition : list) {
                String parName = partition.getPartitionName();
                if(parName.equalsIgnoreCase(partitionName)) {
                	Method[] methods = Partition.class.getDeclaredMethods();
                	for(Method method : methods) {
                		if(method.getName().equalsIgnoreCase("get" + fieldOfInterest)) {
                			Object value = method.invoke(partition);
                			if(value instanceof String) {
                				System.out.println("value=" + value.toString());
                				if(expectValue.contains(value.toString())) {
                					returnValue = Constants.OK;
                				} else {
                					returnValue = Constants.CRITICAL;
                					response.append("expect value: " + expectValue);
                				}
                			} else {
                				returnValue = CommonUtil.getStatus(Long.valueOf(value.toString()), longWarningCap, longCriticalCap, returnValue);
                				response.append("warning: " + longWarningCap + "    critical: " + longCriticalCap);
                			}
                			response.insert(0, fieldOfInterest + ": " + value.toString() + "\n");
                			return returnValue;
                		}
                	}
                }
            }
        } catch (Exception e) {
            response.append(Constants.retrieveDataException + " -  " + e.toString());
            CommonUtil.printStack(e.getStackTrace(), response);
            returnValue = Constants.UNKNOWN;
            e.printStackTrace();
        }
        return returnValue;
    }

}

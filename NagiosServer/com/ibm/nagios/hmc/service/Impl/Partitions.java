package com.ibm.nagios.hmc.service.Impl;

import java.io.InputStream;
import java.io.InputStreamReader;
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

public class Partitions implements Action {
	public static final String API = "/rest/api/uom/LogicalPartition/quick/All";
	
	@Override
	public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
		int returnValue = Constants.OK;
		int runningNum = 0;
		int notActivedNum = 0;
		int errorNum = 0;
		
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
			
			List<Partition> list = new ArrayList<Partition>();
			InputStreamReader rio = null;
			Type listType = new TypeToken<List<Partition>>() {}.getType();

			rio = new InputStreamReader(response_is);

			Gson gson = new Gson();
			JsonReader jr = gson.newJsonReader(rio);

			list = gson.fromJson(jr, listType);
			for(Partition partition : list) {
				String state = partition.getPartitionState();
				switch(state) {
				case "running": runningNum++;
					break;
				case "not actived": notActivedNum++;
					break;
				case "error": errorNum++;
					break;
				default:
					break;
				}
				response.append("\nName: " + partition.getPartitionName()+ "    State: " + partition.getPartitionState() + 
						"    Type: "+ partition.getPartitionType());
			}
			response.insert(0, "error: " + errorNum + "    not active: " + notActivedNum + "    running: " + runningNum);
			if(errorNum > 0) {
				returnValue = Constants.CRITICAL;
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

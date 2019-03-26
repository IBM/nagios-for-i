package com.ibm.nagios.service.impl;

import java.util.ArrayList;
import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.BinaryConverter;
import com.ibm.nagios.config.util.Base64Coder;
import com.ibm.nagios.model.Disk;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.CommonUtil;
import com.ibm.nagios.util.DiskXMLReader;
import com.ibm.nagios.util.HostConfigInfo;
import com.ibm.nagios.util.QYHCHCOP;
import com.ibm.nagios.util.StatusConstants;

public class DiskConfig implements Action {
	private final static int Hardware_OK = 11;
	
	private final static String STATUS[] = new String[]{
		"Unknow", 							// 0
		"Not Operational", 					// 1
		"Not Ready", 						// 2
		"Busy", 							// 3
		"Read/Write Protected",				// 4
		"Write Protected",					// 5
		"Parity Failed",					// 6
		"Parity Unprotected",				// 7
		"Parity Rebuilding",				// 8
		"Performance Degraded",				// 9
		"Redundant Hardware Failure",		// 10
		"Hardware OK"						// 11
	};
	
	public DiskConfig() {
	}

	public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
		int returnValue = StatusConstants.UNKNOWN;
		String system = args.get("-H");
		String userID = HostConfigInfo.getSSTUserID(system);
		String pass = HostConfigInfo.getSSTPassword(system);
		if(userID == null || pass == null) {
			response.append("SST user profile not set");
			return returnValue;
		}
		int userLen = userID.length();
		String password = Base64Coder.decodeString(pass);
		int passLen = password.length();
		final int ccsid = as400.getCcsid();
		QYHCHCOP pgmCall = null;
		
		AS400Text userText = new AS400Text(userLen, ccsid, as400);
		AS400Text passText = new AS400Text(passLen, ccsid, as400);
		
		try {
			pgmCall = new QYHCHCOP();
			pgmCall.setParameter(pgmCall.USER_ID, userText.toBytes(userID));
			pgmCall.setParameter(pgmCall.USER_ID_LEN, BinaryConverter.intToByteArray(userLen));
			pgmCall.setParameter(pgmCall.PASSWORD, passText.toBytes(password));
			pgmCall.setParameter(pgmCall.PASSWOD_LEN, BinaryConverter.intToByteArray(passLen));
			String receiver = pgmCall.run(as400, response);
			if(receiver == null) {
				return returnValue;
			}
				
			ArrayList<Disk> disks = DiskXMLReader.getDiskConfigInfo(receiver.trim());
	        int ASPNum;
	        int unitNum;
	        String resourceName = null;
	        int status;
	        int warningStatus = 0;
	        returnValue = StatusConstants.OK;
	        
	        for(int i=0; i<disks.size(); i++) {
	        	Disk disk = disks.get(i);
	        	ASPNum = disk.getASPNum();
	            unitNum = disk.getDiskNum();
	            resourceName = disk.getResourceName();
	            status = disk.getStatus();
	            if(status!=Hardware_OK && returnValue!=StatusConstants.WARN) {
	            	returnValue = StatusConstants.WARN;
	            	warningStatus = status;
	            }
	            response.append("Unit " + ASPNum + "-" + unitNum + ": " + resourceName + " Status: " + STATUS[status] + "\n");
	        }
	        if(returnValue == StatusConstants.OK) {
	        	response.insert(0, "Disk Status: OK\n");
	        }
	        else if(returnValue == StatusConstants.WARN) {
	        	response.insert(0, "Disk Status: " + STATUS[warningStatus] + "\n");
	        }
		} catch (Exception e) {
			response.setLength(0);
			response.append(StatusConstants.retrieveDataException + "| " + e.getMessage());
			CommonUtil.printStack(e.getStackTrace(), response);
			e.printStackTrace();
		}
		finally {
			userText = null;
			passText = null;
			pgmCall = null;
		}
		return returnValue;
	}
}
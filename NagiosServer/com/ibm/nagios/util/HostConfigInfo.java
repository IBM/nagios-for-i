package com.ibm.nagios.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.ibm.nagios.config.HostConfig;
import com.ibm.nagios.config.util.Base64Coder;
import com.ibm.nagios.config.util.UserInfo;

/*
 * load the info of file Nagios.host.java.config.ser
 */
public class HostConfigInfo {
	private final static String CUST_PROFILE = "/usr/local/nagios/var/profile.csv";
	private static HashMap<String, UserInfo> hosts = null;
	private static HashMap<String, UserInfo> sst = null;

	public static boolean load() {
		try {
			HostConfig.load();
			hosts = HostConfig.getHosts();
			sst = HostConfig.getSST();
	        
			//bulk load: load profile information from profile.csv
	        File customUserProfile = new File(CUST_PROFILE);
	        if(customUserProfile.isFile() && customUserProfile.exists()) {
	        	InputStreamReader read = new InputStreamReader(
                        new FileInputStream(customUserProfile), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(read);
                String line = null;
                String system = null;
                String user = null;
                String pwd = null;
                String type = null;
                while ((line=bufferedReader.readLine()) != null) {
                	line = line.trim();
                	if(line.startsWith("#") == true) {
                		continue;
                	}
                	String[] elem = line.split("[,;]");
                	if(elem.length != 4) {
                		System.err.println("HostConfig-load(): bad format of profile.csv");
                		bufferedReader.close();
                		return true;//DO NOT let the loading failure stop the server initialization
                	}
                	system = elem[0];
                	user = elem[1];
                	pwd = elem[2];
                	type = elem[3];
                	if(type.equalsIgnoreCase("host")) {
                		hosts.put(system, new UserInfo(user, Base64Coder.encodeString(pwd)));
                	} 
                	else if(type.equalsIgnoreCase("sst")) {
                		sst.put(system, new UserInfo(user, Base64Coder.encodeString(pwd)));
                	}
                }
                HostConfig.save();
                bufferedReader.close();
	        }
	        return true;
		}
		catch (Exception e) {
			System.err.println("HostConfig-load(): " + e.toString());
		}
		return false;
	}
	
	public static String getUserID(String system) {
		UserInfo userInfo = hosts.get(system);
		if(userInfo != null)
			return userInfo.getUser();
		else
			return null;
	}
	
	public static String getPassword(String system) {
		UserInfo userInfo = hosts.get(system);
		if(userInfo != null)
			return userInfo.getPassword();
		else
			return null;
	}
	
	public static String getSSTUserID(String system) {
		UserInfo userInfo = sst.get(system);
		if(userInfo != null)
			return userInfo.getUser();
		else
			return null;
	}
	
	public static String getSSTPassword(String system) {
		UserInfo userInfo = sst.get(system);
		if(userInfo != null)
			return userInfo.getPassword();
		else
			return null;
	}
}

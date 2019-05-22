package com.ibm.nagios.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.SecureAS400;
import com.ibm.nagios.config.util.Base64Coder;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.ActionFactory;
import com.ibm.nagios.util.HostConfigInfo;
import com.ibm.nagios.util.Constants;

public class CheckIBMiStatus {
	private static HashMap<String, String> argsMap = new HashMap<String, String>();

	static Socket socket = null;
    
	public static void main(String args[]) {
		int retValue = Constants.UNKNOWN;
		try {
			ParseArgs(args);
			socket = new Socket(Constants.SERVER, Constants.PORT);
			socket.setReuseAddress(true);
			socket.setSoLinger(true, 0);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(argsMap);
			socket.shutdownOutput();
			
			InputStream is = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String result = null;
			if((result=br.readLine()) != null){
				String message;
				while((message=br.readLine()) != null) {
					System.out.println(message);
				}
				retValue = Integer.parseInt(result);
			}
		} catch (IOException e) {
			try {
				String metric = argsMap.get("-M");
				String system = argsMap.get("-H");
				Action action = ActionFactory.get(metric);
				if(!HostConfigInfo.load()) {
					System.err.println("load file Nagios.host.java.config.ser error");
					System.exit(retValue);
				}
				String user = HostConfigInfo.getUserID(system);
				String pass = HostConfigInfo.getPassword(system);
				if(user==null || pass==null) {
					System.err.println("Host user profile not set");
					System.exit(retValue);
				}
				String password = Base64Coder.decodeString(pass);
				argsMap.put("-U", user);
				argsMap.put("-P", password);
				AS400 as400 = null;
				String ssl = argsMap.get("-SSL");
				if(ssl!=null && ssl.equalsIgnoreCase("Y")) {
					as400 = new SecureAS400(system, user, password);
				} else {
					as400 = new AS400(system, user, password);
				}
				as400.setGuiAvailable(false);
				as400.validateSignon();
				StringBuffer response = new StringBuffer();
				retValue = action.execute(as400, argsMap, response);
				System.out.println(response);
			} catch (Exception e1) {
				retValue = Constants.UNKNOWN;
				e1.printStackTrace();
			}
		}
		finally {
			try {
				if(socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.exit(retValue);
	}
	
	private static void ParseArgs(String argv[]) {
		String key = "";
		String value = "";
		for(int i=0; i<argv.length; i++) {
			if(argv[i].startsWith("-")) {
				key = argv[i].toUpperCase();
				value = "";
			}
			else {
				value += argv[i];
				if(key.equalsIgnoreCase("-a")) {
					ParseArgs(value.split(" "));
				} else
					argsMap.put(key, value);
			}
		}
	}
}

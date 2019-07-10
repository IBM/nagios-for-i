package com.ibm.nagios;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import com.ibm.as400.access.AS400;
import com.ibm.nagios.config.util.Base64Coder;
import com.ibm.nagios.util.AS400Connection;
import com.ibm.nagios.util.CommonUtil;
import com.ibm.nagios.util.HostConfigInfo;
import com.ibm.nagios.util.Constants;

public class ConnectToSystem implements Runnable {
	Socket socket;
	
	public ConnectToSystem(Socket socket) {
		this.socket = socket;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		StringBuffer response = new StringBuffer();
		int retval = Constants.UNKNOWN;
		try {
			InputStream is = socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			HashMap<String, String> args = (HashMap<String, String>) ois.readObject();
			String systemName = args.get("-H");
			if(systemName == null) {
				String metric = args.get("-M");
				if(metric.equalsIgnoreCase("DaemonServer")) {	//check daemon server status
					CheckIBMiStatus check = new CheckIBMiStatus(null, args);
					check.run(response);
				} else if(metric.equalsIgnoreCase("RefreshProfile")) {//reload HostConfigInfo
					HostConfigInfo.load();
				}
			}
			else {	//check IBM i status
				//load user profile and password from Nagios.host.java.config.ser
				String user = HostConfigInfo.getUserID(systemName);
				String pass = HostConfigInfo.getPassword(systemName);
				if(user==null || pass==null) {
					response.append("Host user profile not set");
					return;
				}
				String password = Base64Coder.decodeString(pass);
				args.put("-U", user);
				args.put("-P", password);
				AS400Connection as400Conn = new AS400Connection();
				AS400 as400 = as400Conn.getAS400Object(systemName, user, password, response, args.get("-SSL"));
				if(as400 == null) {
					response.append("\nConnectToSystem - run(): as400 is null");
				}
				else {
					as400.setGuiAvailable(false);
//					Server.metricPool.execute(new CheckIBMiStatus(as400, socket, args));
					CheckIBMiStatus check = new CheckIBMiStatus(as400, args);
					retval = check.run(response);
				}
			}
		} catch (Exception e) {
			response.append(Constants.retrieveDataException + " - " + e.toString());
			CommonUtil.printStack(e.getStackTrace(), response);
			System.err.println("ConnectToSystem - run(): " + e.toString());
			e.printStackTrace();
		} finally {
			try {
				PrintResponse(retval, response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			response = null;
		}
	}
	
	void PrintResponse(Integer retVal, StringBuffer response) throws IOException {
		OutputStream os = socket.getOutputStream();
		PrintWriter pw = new PrintWriter(os);
		if(retVal != null) {
			pw.write(retVal + "\n");
		} else {
			pw.write(Integer.toString(Constants.UNKNOWN) + "\n");
		}
		pw.write(response.toString());
		pw.flush();
		
		pw.close();
		os.close();
	}
}

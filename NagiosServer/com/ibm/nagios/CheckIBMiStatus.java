package com.ibm.nagios;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import com.ibm.as400.access.AS400;

public class CheckIBMiStatus {
	AS400 as400;
	Socket socket;
	Map<String, String> args;
	
	public CheckIBMiStatus(AS400 as400, Socket socket, Map<String, String> args) {
		this.as400 = as400;
		this.socket = socket;
		this.args = args;
	}
	
	public void run() {
		try {
			StringBuffer response = new StringBuffer();
			RequestHandler handler = new RequestHandler();
			int retVal = handler.process(as400, args, response);
			OutputStream os = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(os);
			pw.write(Integer.toString(retVal) + "\r\n");
			pw.write(response.toString());
			pw.flush();
			
			response = null;
			pw.close();
			os.close();
			socket.close();
		} catch (IOException e) {
			System.err.println("ERROR on system " + as400.getSystemName() + ": " + e.toString());
		}
	}
}

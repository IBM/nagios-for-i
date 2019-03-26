package com.ibm.nagios.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class CheckDaemonStatus {
	private static final int PORT = 8888;
	private static final String SERVER = "localhost";
	
	private static HashMap<String, String> argsMap = new HashMap<String, String>();
	
	public static String retrieveDataException = "Exception";
	public static String retrieveDataError = "Error";
	public static int OK = 0;
	public static int WARN = 1;
	public static int CRITICAL = 2;
	public static int UNKNOWN = 3;
	static Socket socket = null;
    
	public static void main(String args[]) {
		int retValue = UNKNOWN;
		try {
			ParseArgs(args);
			socket = new Socket(SERVER, PORT);
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
			System.out.println("Daemon server went wrong | Please restart the server");
		}
		finally {
			try {
				if(socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				System.err.println(e.toString());
			}
		}
//		if(retValue != 0) {
//			try {
//				//String []cmd = {"/bin/bash", "/usr/local/nagios/libexec/server_restart.sh"};
//				String cmd = "/usr/local/nagios/libexec/server_restart.sh";
//				Runtime.getRuntime().exec(cmd);
//				Thread.sleep(500);
//			} catch (IOException | InterruptedException e) {
//				System.out.println(e.toString());
//			}
//		}
		System.exit(retValue);
	}
	
	private static void ParseArgs(String argv[]) {
		String key = "";
		String value = "";
		for(int i=0; i<argv.length; i++) {
			if(argv[i].startsWith("-")) {
				key = argv[i].toUpperCase();
				value = "";
				continue;
			}
			else {
				value += argv[i];
			}
			argsMap.put(key, value);
		}
	}
}
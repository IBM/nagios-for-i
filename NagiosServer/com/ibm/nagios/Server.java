package com.ibm.nagios;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ibm.nagios.util.Constants;
import com.ibm.nagios.util.CustomPluginFactory;
import com.ibm.nagios.util.HostConfigInfo;

public class Server {	
	public static ExecutorService workers = Executors.newCachedThreadPool();
	
	@SuppressWarnings("resource")
	public static void main(String args[]) {
		try {
			ServerSocket serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress(Constants.PORT));
			if(!HostConfigInfo.load()) {
				System.err.println("Nagios server initialized failed");
				return;
			}
			CustomPluginFactory.load();
			System.out.println("Nagios server initialized successfully");
			System.setProperty("com.ibm.as400.access.AS400.guiAvailable", "false");
			while(true) {
				Socket socket = serverSocket.accept();
				workers.execute(new ConnectToSystem(socket)); //start thread to get as400 connection
			}		
        } catch (Exception e) {
        	System.err.println("Server - main(): " + e.toString());
        	e.printStackTrace();
        }
	}
}

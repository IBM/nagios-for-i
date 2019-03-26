package com.ibm.nagios;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ibm.nagios.util.HostConfigInfo;

public class Server {
	private static final int THREAD_POOL_SIZE = 130;
	private static final int PORT = 8888;
	
	public static ExecutorService systemPool = Executors.newCachedThreadPool();
	public static ExecutorService metricPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	
	@SuppressWarnings("resource")
	public static void main(String args[]) {
		try {
			ServerSocket serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress(PORT));
			HostConfigInfo host = new HostConfigInfo();
			if(!host.load()) {
				System.err.println("Nagios server initialized failed");
				return;
			}
			System.out.println("Nagios server initialized successfully");
			System.setProperty("com.ibm.as400.access.AS400.guiAvailable", "false");
			//System.setProperty("com.ibm.as400.access.Trace.category", "ALL");
			//System.setProperty("com.ibm.as400.access.Trace.file", "/tmp/out.txt");
			while(true) {
				Socket socket = serverSocket.accept();
				systemPool.execute(new ConnectToSystem(socket)); //start thread to get as400 connection
			}		
        } catch (Exception e) {
        	System.err.println("Server - main(): " + e.getMessage());
        	e.printStackTrace();
        }
	}
}

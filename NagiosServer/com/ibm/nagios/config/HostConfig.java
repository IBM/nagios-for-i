package com.ibm.nagios.config;
import java.io.BufferedInputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.ibm.nagios.Server;
import com.ibm.nagios.client.CheckIBMiStatus;
import com.ibm.nagios.config.util.Base64Coder;
import com.ibm.nagios.config.util.UserInfo;

public class HostConfig {
	private final static String DIRECTORY = "/usr/local/nagios/";
	private final static String FILENAME = "Nagios.host.java.config.ser";
	private static HashMap<String, HashMap<String, UserInfo>> cache = new HashMap<String, HashMap<String, UserInfo>>();
	private static HashMap<String, UserInfo> hosts = new HashMap<String, UserInfo>();
	private static HashMap<String, UserInfo> sst = new HashMap<String, UserInfo>();
	
	public static void main(String args[]) {
		if(args.length == 0) {
			System.out.println("Input parameters error");
			return;
		}
		String action = args[0];
		Console console = System.console();
		String type = null;
		if(action.equalsIgnoreCase("-i")) {
			if(args.length != 2) {
				System.out.println("Input parameter error");
				return;
			}
			type = args[1];
			if(type!=null && (type.equalsIgnoreCase("host") || type.equalsIgnoreCase("sst"))) {
				String hostAddr = console.readLine("Input the host address(IP):").trim();
				String user = console.readLine("Input the userID:").trim();
				char[] password1 = console.readPassword("Input the password:");
				char[] password2 = console.readPassword("Confirm the password:");
				String pass1 = new String(password1);
				pass1= pass1.trim();
				String pass2 = new String(password2);
				pass2= pass2.trim();
				if(!pass1.equals(pass2)) {
					System.out.println("The two passwords do not match");
					return;
				}

				if(Insert(hostAddr, user, pass1.trim(), type.trim())) {
					System.out.println("Insert the item successfully");
					System.out.println("Host: " + hostAddr + "    User: " + user);
				}
				else {
					System.out.println("Insert host failed");
				}
			}
			else {
				System.out.println("Input parameters error");
			}
		} else if(action.equalsIgnoreCase("-d")) {
			if(args.length != 2) {
				System.out.println("Input parameters error");
				return;
			}
			type = args[1];
			if(type!=null && (type.equalsIgnoreCase("host") || type.equalsIgnoreCase("sst"))) {
				String rmvHost = console.readLine("Input the host address(IP) you want to remove:");
				String confirmation = console.readLine("Are you confirm to remove the item? Y/N:");
				if(confirmation.equalsIgnoreCase("n")) {
					return;
				}
				if(remove(rmvHost, type)) {
					System.out.println("Removed the item " + rmvHost);
				}
				else {
					System.out.println("Removed the item failed");
				}
			}
			else {
				System.out.println("Input parameters error");
			}
		} else if(action.equalsIgnoreCase("-a")) {
			ListAll();
		} else if(action.equalsIgnoreCase("-h")) {
			showHelp();
		} else {
			System.out.println("Error input parameters");
			showHelp();
		}
	}
	
	private static boolean Insert(String hostAddr, String userID, String password, String type) {
		try
        {	
			if(load()) {
				if(type.equalsIgnoreCase("host")) {
					hosts.put(hostAddr, new UserInfo(userID, Base64Coder.encodeString(password)));
				}
				else if(type.equalsIgnoreCase("sst")) {
					sst.put(hostAddr, new UserInfo(userID, Base64Coder.encodeString(password)));
				}
				if(save()) {
					refreshProfile();
					return true;
				}
			}
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
		return false;
	}
	
	private static boolean remove(String hostAddr, String type) {
		try {
			if(load()) {
				if(type.equalsIgnoreCase("host")) {
					hosts.remove(hostAddr);
				}
				else if(type.equalsIgnoreCase("sst")) {
					sst.remove(hostAddr);
				}
				if(save()) {
					refreshProfile();
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static boolean load() throws IOException, ClassNotFoundException {
		File file = new File(DIRECTORY + FILENAME);
    	if(!file.exists()) {
    		File dir = new File(DIRECTORY);
    		if(!dir.exists() && !dir.isDirectory()) {
    			System.out.println("The directory " + DIRECTORY + "doesn't exist");
    			return false;
    		}
    		file.createNewFile();
    		save();
    		file.setReadable(true, false);	//set authority to 666
    		file.setWritable(true, false);
    		file.setExecutable(false, false);
    	}
        InputStream bis = new BufferedInputStream(new FileInputStream(file));
        GZIPInputStream gzis = new GZIPInputStream(bis);
        ObjectInputStream ois = new ObjectInputStream(gzis);
        cache = (HashMap<String, HashMap<String, UserInfo>>)ois.readObject();
        hosts = cache.get("host");
        sst = cache.get("sst");
        ois.close();
        return true;
	}
	
	public static boolean save() throws IOException {
        FileOutputStream fos = new FileOutputStream(DIRECTORY + FILENAME);
        GZIPOutputStream gzos = new GZIPOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(gzos);
        cache.put("host", hosts);
        cache.put("sst", sst);
        oos.writeObject(cache);
        oos.flush();
        oos.close();
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	private static void ListAll() {
		try {
			load();
			Iterator iter = hosts.entrySet().iterator();
			System.out.println("Host profiles:");
			while(iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String host = (String) entry.getKey();
				UserInfo userInfo = (UserInfo) entry.getValue();
				System.out.println(host + "  " + userInfo.getUser());
			}
			iter = sst.entrySet().iterator();
			System.out.println("SST profiles:");
			while(iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String host = (String) entry.getKey();
				UserInfo userInfo = (UserInfo) entry.getValue();
				System.out.println(host + "  " + userInfo.getUser());
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
	public static HashMap<String, UserInfo> getHosts() {
		return hosts;
	}
	
	public static HashMap<String, UserInfo> getSST() {
		return sst;
	}
	
	private static void showHelp() {
		System.out.println("-i [host | sst] Insert a host. If the host exists, update the information");
		System.out.println("   host: Insert an AS400   sst: Insert a SST(Used for checking disk configuration)");
		System.out.println("-d [host | sst] Delete a host");
		System.out.println("   host: Remove an AS400   sst: Remove a SST");
		System.out.println("-a List all the hosts");
	}
	
	private static void refreshProfile() {
		try {
			Socket socket = new Socket(CheckIBMiStatus.SERVER, Server.PORT);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			HashMap<String, String> argsMap = new HashMap<String, String>();
			argsMap.put("-M", "RefreshProfile");
			oos.writeObject(argsMap);
			socket.close();
		} catch (Exception e) {
			//The daemon server might not active
		}
		
	}
}

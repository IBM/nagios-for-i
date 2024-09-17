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

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.SocketProperties;
import com.ibm.nagios.config.util.Base64Coder;
import com.ibm.nagios.config.util.UserInfo;
import com.ibm.nagios.util.Constants;

public class HostConfig {
    private final static String OLD_DIRECTORY = "/usr/local/nagios/";
    private final static String NEW_DIRECTORY = "/usr/local/nagios/var/";//change the directory for XI permission problem
    private final static String FILENAME = "Nagios.host.java.config.ser";
    private static HashMap<String, HashMap<String, UserInfo>> cache = new HashMap<String, HashMap<String, UserInfo>>();
    private static HashMap<String, UserInfo> hosts;
    private static HashMap<String, UserInfo> sst;
    private static HashMap<String, UserInfo> hmc;

    public static void main(String args[]) {
        if (args.length == 0) {
            System.out.println(args.length + " parameters is invalid. Input parameters error");
            showHelp();
            return;
        }
        String action = args[0];
        Console console = System.console();
        String type = null;

        if (action.equalsIgnoreCase("-i") || action.equalsIgnoreCase("-d")) {
            if (args.length < 2 || args[1] == null) {
                type = console.readLine("Please select the profile: \"host\", \"sst\" or \"hmc\":").trim();
            } else {
                if (args[1].equalsIgnoreCase("host") || args[1].equalsIgnoreCase("sst") || args[1].equalsIgnoreCase("hmc")) {
                    type = args[1];
                } else {
                    type = console.readLine("Please select the profile: \"host\", \"sst\" or \"hmc\":").trim();
                }
            }
            if (!type.equalsIgnoreCase("host") && !type.equalsIgnoreCase("sst") && !type.equalsIgnoreCase("hmc")) {
                System.out.println("Input parameters error. Please check your input and try again.");
                System.out.println("\"host\" for AS400 user profile and \"sst\" for SST user profile and \"hmc\" for HMC user profile.");
                return;
            }
        }
        if (action.equalsIgnoreCase("-i")) {
            if (args.length >= 5 && args[2] != null && args[3] != null && args[4] != null) {
                if (Insert(args[2].trim(), args[3].trim(), args[4].trim(), type.trim())) {
                    System.out.println("Insert the item successfully");
                    System.out.println("Host: " + args[2] + "    User: " + args[3]);
                } else {
                    System.out.println("Insert host failed");
                }
            } else {
                String hostAddr = console.readLine("Input the host address(IP):").trim();
                String user = console.readLine("Input the userID:").trim();
                char[] password1 = console.readPassword("Input the password:");
                char[] password2 = console.readPassword("Confirm the password:");
                String pass1 = new String(password1);
                pass1 = pass1.trim();
                String pass2 = new String(password2);
                pass2 = pass2.trim();
                if (!pass1.equals(pass2)) {
                    System.out.println("The two passwords do not match");
                    return;
                }
                if (Insert(hostAddr, user, pass1.trim(), type.trim())) {
                    System.out.println("Insert the item successfully");
                    System.out.println("Host: " + hostAddr + "    User: " + user);
                } else {
                    System.out.println("Insert host failed");
                }
            }
        } else if (action.equalsIgnoreCase("-d")) {
            String rmvHost = console.readLine("Input the host address(IP) you want to remove:");
            String confirmation = console.readLine("Are you confirm to remove the item? Y/N:");
            if (confirmation.equalsIgnoreCase("n")) {
                return;
            }
            if (remove(rmvHost, type)) {
                System.out.println("Removed the item " + rmvHost);
            } else {
                System.out.println("Removed the item failed");
            }
        } else if (action.equalsIgnoreCase("-a")) {
            ListAll();
        } else if (action.equalsIgnoreCase("-h")) {
            showHelp();
        } else {
            System.out.println("Error input parameters");
            showHelp();
        }
    }

    private static boolean Insert(String hostAddr, String userID, String password, String type) {
        try {
            if (type.equalsIgnoreCase("host")) {
                AS400 as400 = new AS400(hostAddr, userID, password);
                SocketProperties socketProps = new SocketProperties();
                socketProps.setLoginTimeout(15000); //set timeout to 15 seconds
                as400.setSocketProperties(socketProps);
                as400.setGuiAvailable(false);
                try {
                    as400.validateSignon();
                } catch (Exception e) {
                    System.out.println(e.toString());
                    return false;
                }
            }
            if (load()) {
                if (type.equalsIgnoreCase("host")) {
                    hosts.put(hostAddr, new UserInfo(userID, Base64Coder.encodeString(password)));
                } else if (type.equalsIgnoreCase("sst")) {
                    sst.put(hostAddr, new UserInfo(userID, Base64Coder.encodeString(password)));
                } else if (type.equalsIgnoreCase("hmc")) {
                    hmc.put(hostAddr, new UserInfo(userID, Base64Coder.encodeString(password)));
                }
                if (save()) {
                    refreshProfile();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
            for (StackTraceElement ele : e.getStackTrace()) {
                System.out.println(ele);
            }
        }
        return false;
    }

    private static boolean remove(String hostAddr, String type) {
        try {
            if (load()) {
                if (type.equalsIgnoreCase("host")) {
                    hosts.remove(hostAddr);
                } else if (type.equalsIgnoreCase("sst")) {
                    sst.remove(hostAddr);
                }
                if (save()) {
                    refreshProfile();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean load() throws IOException, ClassNotFoundException {
        File file = new File(NEW_DIRECTORY + FILENAME);
        if (!file.exists()) {
            File dir = new File(NEW_DIRECTORY);
            if (!dir.exists() && !dir.isDirectory()) {
                System.out.println("The directory " + NEW_DIRECTORY + "doesn't exist");
                return false;
            }
            file.createNewFile();

            //if the old ser file exists, copy the content to new ser file
            File oldFile = new File(OLD_DIRECTORY + FILENAME);
            if (oldFile.exists()) {
                InputStream bis = new BufferedInputStream(new FileInputStream(oldFile));
                GZIPInputStream gzis = new GZIPInputStream(bis);
                ObjectInputStream ois = new ObjectInputStream(gzis);
                cache = (HashMap<String, HashMap<String, UserInfo>>) ois.readObject();
                hosts = cache.get("host");
                sst = cache.get("sst");
                hmc = cache.get("hmc");
                ois.close();
            }
            save();
            file.setReadable(true, false);    //set authority to 666
            file.setWritable(true, false);
            file.setExecutable(false, false);
        }
        InputStream bis = new BufferedInputStream(new FileInputStream(file));
        GZIPInputStream gzis = new GZIPInputStream(bis);
        ObjectInputStream ois = new ObjectInputStream(gzis);
        cache = (HashMap<String, HashMap<String, UserInfo>>) ois.readObject();
        hosts = cache.get("host");
        sst = cache.get("sst");
        hmc = cache.get("hmc");

        if (hosts == null) {
            hosts = new HashMap<String, UserInfo>();
        }
        if (sst == null) {
            sst = new HashMap<String, UserInfo>();
        }
        if (hmc == null) {
            hmc = new HashMap<String, UserInfo>();
        }

        ois.close();
        return true;
    }

    public static boolean save() throws IOException {
        FileOutputStream fos = new FileOutputStream(NEW_DIRECTORY + FILENAME);
        GZIPOutputStream gzos = new GZIPOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(gzos);
        cache.put("host", hosts);
        cache.put("sst", sst);
        cache.put("hmc", hmc);
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
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String host = (String) entry.getKey();
                UserInfo userInfo = (UserInfo) entry.getValue();
                System.out.println(host + "  " + userInfo.getUser());
            }

            iter = sst.entrySet().iterator();
            System.out.println("\nSST profiles:");
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String host = (String) entry.getKey();
                UserInfo userInfo = (UserInfo) entry.getValue();
                System.out.println(host + "  " + userInfo.getUser());
            }

            iter = hmc.entrySet().iterator();
            System.out.println("\nHMC profiles:");
            while (iter.hasNext()) {
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

    public static HashMap<String, UserInfo> getHMC() {
        return hmc;
    }

    private static void showHelp() {
        System.out.println("-i [host | sst] Insert a host. If the host exists, update the information");
        System.out.println("   host: Insert an AS400 profile   sst: Insert a SST(Used for checking disk configuration)");
        System.out.println("-d [host | sst] Delete a host");
        System.out.println("   host: Remove an AS400 profile   sst: Remove a SST");
        System.out.println("-a List all the hosts");
    }

    private static void refreshProfile() {
        try {
            Socket socket = new Socket(Constants.SERVER, Constants.PORT);
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

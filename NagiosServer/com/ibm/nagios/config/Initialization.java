package com.ibm.nagios.config;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * used for initializing Nagios core configuration files. 
 */
public class Initialization {
	private final static String OBJECTS = "/usr/local/nagios/etc/objects/";
	private final static String COMMANDS_CFG = "commands.cfg";
	private final static String LOCALHOST_CFG = "localhost.cfg";
	private static final String TEMPLATES_CFG = "templates.cfg";
	private final static String COMMANDS = "commands";
	private final static String LOCALHOST = "localhost";
	private static final String TEMPLATES = "templates";
	
	public static void main(String[] args) {	
		try {
			File cmdCfg = new File(OBJECTS + COMMANDS_CFG);
			if(cmdCfg.exists()) {
				updateCmdCfg(cmdCfg, COMMANDS);
			}
			else {
				System.out.println("Target file " + cmdCfg.getAbsolutePath() + " doesn't exist.");
				System.out.println("Initialize commands.cfg failed");
			}
			
			File locHostCfg = new File(OBJECTS + LOCALHOST_CFG);
			if(locHostCfg.exists()) {
				updateCmdCfg(locHostCfg, LOCALHOST);
			}
			else {
				System.out.println("Target file " + locHostCfg.getAbsolutePath() + " doesn't exist.");
				System.out.println("Initialize localhost.cfg failed");
			}
			
			File tmpCfg = new File(OBJECTS + TEMPLATES_CFG);
			if(tmpCfg.exists()) {
				updateCmdCfg(tmpCfg, TEMPLATES);
			}
			else {
				System.out.println("Target file " + tmpCfg.getAbsolutePath() + " doesn't exist.");
				System.out.println("Initialize templates.cfg failed");
			}
		} catch (Exception e) {
			System.err.println("Initialize failed");
			System.out.println("ERROR: " + e.toString());
		}
	}

	@SuppressWarnings("resource")
	private static void updateCmdCfg(File cfgFile, String type) throws IOException {
		boolean initFlag = false;
		BufferedReader reader = new BufferedReader(new FileReader(cfgFile));
		String line = null;
		if(type.equalsIgnoreCase("commands")) {
			while((line = reader.readLine()) != null) {
				if((line.lastIndexOf("IBM i STATUS CHECK COMMANDS")) != -1) {
					System.out.println("The commands.cfg has already been Initialized");
					initFlag = true;
				}
			}
			if(!initFlag) {
				StringBuilder commands = new StringBuilder();
				InitCommands(commands);
				FileWriter writer = new FileWriter(cfgFile, true);
		        writer.write(commands.toString());
		        writer.close();
		        System.out.println("The commands.cfg initialized successfully");
			}
		}
		else if(type.equalsIgnoreCase("localhost")){
			while((line = reader.readLine()) != null) {
				if((line.lastIndexOf("Nagios Daemon Server")) != -1) {
					System.out.println("The localhost.cfg has already been Initialized");
					initFlag = true;
				}
			}
			if(!initFlag) {
				StringBuilder daemon = new StringBuilder();
				InitDaemon(daemon);
				FileWriter writer = new FileWriter(cfgFile, true);
		        writer.write(daemon.toString());
		        writer.close();
		        System.out.println("The localhost.cfg initialized successfully");
			}
		}
		else if(type.equalsIgnoreCase("templates")){
			while((line = reader.readLine()) != null) {
				if((line.lastIndexOf("Nagios for IBM i Daemon Server template")) != -1) {
					System.out.println("The templates.cfg has already been Initialized");
					initFlag = true;
				}
			}
			if(!initFlag) {
				StringBuilder template = new StringBuilder();
				InitTemplate(template);
				FileWriter writer = new FileWriter(cfgFile, true);
		        writer.write(template.toString());
		        writer.close();
		        System.out.println("The templates.cfg initialized successfully");
			}
		}
	}
	
	private static void InitCommands(StringBuilder commands) {
		commands.append("\r\n################################################################################\r\n");
		commands.append("#\r\n");
		commands.append("# IBM i STATUS CHECK COMMANDS\r\n");
		commands.append("#\r\n");
		commands.append("################################################################################\r\n");
		commands.append("# 'check-cpu-utilization' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-cpu-utilization\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -w $ARG2$ -c $ARG3$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-active-job' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-active-job-num\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -w $ARG2$ -c $ARG3$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-disk-config' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-disk-config\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-disk-usage' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-disk-usage\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -w $ARG2$ -c $ARG3$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-asp-usage' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-asp-usage\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -w $ARG2$ -c $ARG3$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-iasp-usage' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-iasp-usage\r\n");
		commands.append("	   command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -w $ARG2$ -c $ARG3$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-message' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-message\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -lib $ARG2$ -name $ARG3$ -ty $ARG4$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-page-faults' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-page-faults\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -w $ARG2$ -c $ARG3$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-subsystem-jobs' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-subsystem-jobs\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -s $ARG2$ -w $ARG3$ -c $ARG4$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-temp-storage-jobs' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-temp-storage-jobs\r\n");
		commands.append("	   command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -n $ARG2$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-long-run-sql' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-long-run-sql\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -w $ARG2$ -c $ARG3$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-current-logon-users' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-cur-logon-users\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -w $ARG2$ -c $ARG3$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-specific-job-cpu' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-specific-job-cpu\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -j $ARG2$ -w $ARG3$ -c $ARG4$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-cpu-overload-jobs-num' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-cpu-overload-jobs-num\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -w $ARG2$ -c $ARG3$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-specific-message' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-specific-message\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -I $ARG2$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-basic-information' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-ibmi-basic-info\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$\r\n");
		commands.append("}\r\n");
		commands.append("#'custom-sql' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    custom-sql\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_ibmi_status.sh -m $ARG1$ -H $HOSTADDRESS$ -f $ARG2$ -w $ARG3$ -c $ARG4$\r\n");
		commands.append("}\r\n");
		commands.append("#'check-daemon-server' command definition\r\n");
		commands.append("define command{\r\n");
		commands.append("      command_name    check-nagios-daemon-server\r\n");
		commands.append("      command_line    /bin/bash /usr/local/nagios/libexec/check_daemon_status.sh -m $ARG1$\r\n");
		commands.append("}");
	}
	
	private static void InitDaemon(StringBuilder daemon) {
		daemon.append("\r\n\r\n#Define a service to check Nagios Daemon server on the local machine.\r\n");
		daemon.append("#Disable natifications for this service by default, as not all users may have HTTP enabled.\r\n\r\n");
		daemon.append("define service{\r\n");
		daemon.append("\tuse                            daemon-server-service\r\n");
		daemon.append("\thost_name                      localhost\r\n");
		daemon.append("\tservice_description            Daemon Server\r\n");
		daemon.append("\tcheck_command                  check-nagios-daemon-server!DaemonServer\r\n");
		daemon.append("\tnotifications_enabled	        0\r\n");
		daemon.append("\t}");
	}
	
	private static void InitTemplate(StringBuilder template) {
	    template.append("\r\n\r\n#Nagios for IBM i Daemon Server template.\r\n");
	    template.append("define service{\r\n");
	    template.append("\tname                       daemon-server-service\r\n");
	    template.append("\tuse                        generic-service\r\n");
	    template.append("\tmax_check_attempts         4\r\n");
	    template.append("\tnormal_check_interval      0.1\r\n");
	    template.append("\tretry_check_interval       0.1\r\n");
	    template.append("\t register\t                0\r\n");
	    template.append("}");
	  }
}

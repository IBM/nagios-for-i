# nagios-for-i

> [!WARNING]  
> This package is currently no longer supported or maintained. The repository has been archived to reflect this status and may be unarchived if it becomes actively maintained in the future

## Description
Nagios provides enterprise-class Open Source IT monitoring, network monitoring, server and applications monitoring.<br>
We provide several customized plugin for monitoring IBM i systems.<br>
You could refer to the [this link](https://www.ibm.com/support/pages/node/1274296/ ) for more details.
<br><br>
The nagios plugin for i has two methods. 
* You can run the plugin with a daemon server to handle and dispatch the request to worker thread.<br>
You could start the daemon server by script server_start.sh.
* You can also run the plugin without the server. Then every request will be handled by separate processes. It would be more simple but consumes more resources.
<br>
The plugins support to monitor following matrix<br>
<br>
<pre>
  <b>plugin            matrix</b><br>
  CPU               Retrieve the CPU utilization for the entire system<br>
  SpecificJobCPU    Retrieve the CPU usage for a specific job<br>
  DiskUsage         Retrieve the disk usage status<br>
  DiskConfig*       Retrieve the disk health status<br>
  ASPUsage          Retrieve the ASP usage percentage of the entire system<br>
  ActiveJobs        Retrieve the number of active jobs on the system<br>
  BasicInfo         Retrieve the basic information of an IBM i system<br>
  CurSignOnUsers    Retrieve the number of users that currently log on to the system<br>
  LongRunSQL        Retrieve the longest running SQL<br>
  Message           Retrieve the messages from a specific message queue<br>
  SpecificMessage   Retrieve the status whether a specific message ID is found in a specific message queue<br>
  PageFaults        Retrieve the page faults<br>
  SubsystemJobs     Retrieve job information in a specific subsystem<br>
  CustomSQL         The user could leverage SQL services to create self-defined matrix<br>
  TempStorageJobs   Retrieve top N jobs that have the most temp storage usage<br>
  DaemonServer      Retrieve the daemon server status<br>
</pre>
<br>
* DiskConfig is no longer supported in 7.5 and beyond.<br>
<br>
## Installation and upgrade<br>

# Nagios Core

For the version of Nagios Core, you could install the latest plugins by service pack in directory **service pack**.<br>
You could take *Nagios Plugin for IBM i Install and Configure Guidelines.txt* for detail steps.<br>

# Nagios XI

The wizards for monitoring IBM i should be installed in the base shipment for Nagios XI (As of Jan 2021 this is not the case). To update or
install the plugins you should do the following.<br>

- Download the Wizards and store locally<br>
	- [ibmiservice.zip](https://github.com/IBM/nagios-for-i/blob/master/Wizard%20for%20NagiosXI/ibmiservice.zip)<br>
	- [ibmicustomsql.zip](https://github.com/IBM/nagios-for-i/blob/master/Wizard%20for%20NagiosXI/ibmicustomsql.zip)<br>
- Open the web interface.<br>
	- Select the Admin page<br>
	- Select Manage Config Wizards (left hand column of links)<br> 
	- Select Browse to select the zip file
	- Select the Upload and Install button to install the Wizard
	
The wizards should now be available to monitoring the IBM i.

Information from Nagios about installing wizards which has a lot more information can be found [here](https://assets.nagios.com/downloads/nagiosxi/docs/Installing-Nagios-XI-Configuration-Wizards.pdf).


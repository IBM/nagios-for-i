# nagios-for-i
## Overview
Nagios provides enterprise-class Open Source IT monitoring, network monitoring, server and applications monitoring.<br>
We provide several customized plugin for monitoring IBM i systems.<br>
You could refer to the [wiki](https://www.ibm.com/developerworks/community/wikis/home?lang=en#!/wiki/IBM%20i%20Technology%20Updates/page/Nagios%20plugin%20support%20for%20IBM%20i ) for more details.
<br><br>
The nagios plugin for i has two methods. 
* You can run the plugin with a daemon server to handle and dispatch the request to worker thread.<br>
You could start the daemon server by script server_start.sh.
* You can also run the plugin without the server. Then every request will be handled by seperate processes. It would be more simple but consumes more resources.
<br>
The plugins suport to mornitor following martix<br>
<pre>
  <b>plugin            martix</b><br>
  CPU               Retrieve the CPU utilization for the entire system<br>
  SpecificJobCPU    Retrieve the CPU usage for a specific job<br>
  DiskUsage         Retrieve the disk usage status<br>
  DiskConfig        Retrieve the disk health status<br>
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

## NaigosXI Wizards
* ibmicommand.zip <br>
The wizard ibmicommand.zip is mainly used to manage CustomSQL.xml. And also you can preview all the pre-defined commands.
* ibmiservice.zip <br>
You can create the services by wizard ibmiservice.zip. Fill in the IP address and user profile information, and check the command you want to process. Then the services will be generate automatically.

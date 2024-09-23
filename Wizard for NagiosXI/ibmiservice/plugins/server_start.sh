#!/bin/bash
pid=$(ps -ef | grep java | grep "name=nagios" | awk '{print $2}')
if [ "" = "$pid" ] ; then
	echo "Starting Nagios Server"
	nohup java -cp /usr/local/nagios/libexec/jt400.jar:/usr/local/nagios/libexec/gson.jar:/usr/local/nagios/libexec/nagios4i.jar com.ibm.nagios.Server -dname=nagios  > /usr/local/nagios/var/server.log &
	service nagios start
	echo "Nagios Service Started"
else
	echo "The server is already started"
fi

#!/bin/bash
java -cp /usr/local/nagios/libexec/server.jar com.ibm.nagios.config.HostConfig $1 $2

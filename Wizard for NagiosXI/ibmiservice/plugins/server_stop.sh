#!/bin/bash
ps -ef | grep java | grep "name=nagios" | awk '{print $2}' | xargs kill -9
service nagios stop
echo "Nagios Server stopped"

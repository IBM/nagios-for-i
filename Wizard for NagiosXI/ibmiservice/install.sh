#!/bin/bash



# move to the upload target directory
BASEDIR=$(dirname $(readlink -f $0))
cd $BASEDIR

# handle file /usr/local/nagios/etc/objects/CustomSQL.xml
if [ ! -f /usr/local/nagios/etc/objects/CustomSQL.xml ] ; then
	if [ ! -d /usr/local/nagios/etc/objects ] ; then
		mkdir /usr/local/nagios/etc/objects 
	fi
	cp $BASEDIR/CustomSQL.xml  /usr/local/nagios/etc/objects
fi

# handle file /usr/local/nagios/var/profile.csv
if [ ! -f /usr/local/nagios/var/profile.csv ] ; then
	cp $BASEDIR/profile.csv  /usr/local/nagios/var
	chmod 666 /usr/local/nagios/var/profile.csv
	/bin/bash /usr/local/nagios/libexec/ibmi_profile_init.sh
fi

# handle file /usr/local/nagios/var/Nagios.host.java.config.ser
if [ ! -f /usr/local/nagios/var/Nagios.host.java.config.ser ] ; then
	if [ -f /usr/local/nagios/Nagios.host.java.config.ser ] ; then
		cp /usr/local/nagios/Nagios.host.java.config.ser  /usr/local/nagios/var/Nagios.host.java.config.ser
	else
		cp $BASEDIR/Nagios.host.java.config.ser  /usr/local/nagios/var
	fi
	chmod 666 /usr/local/nagios/var/Nagios.host.java.config.ser
fi

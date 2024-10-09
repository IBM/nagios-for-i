# Configuring Host Profiles on Nagios XI

A "host profile" refers to the profile 
that the services will use to run. It consists of the IP address for the system, a 
user name, a password, and a type (host, sst, or hmc). <br>
Nagios XI has multiple ways to set the Host Profiles:
* The most common method will be through the ibmiservice Configuration Wizard. When you enter the information 
for your host it will create the host/service definitions within Nagios XI and also it will 
automatically take the host profile data and create an entry in the 
nagios-for-i configuration file (/usr/local/nagios/var/Nagios.host.java.config.ser).
* The next method is to use the command line interface to manually add the entries into the config file.
It can be used to update profiles and passwords of individual host profiles without affecting what 
services you have defined. Note that this will NOT add anything to the Nagios XI dataset. No new 
entries will be shown in hosts/services. Instead this will ONLY update the Nagios.host.java.config.ser file.
It can be ran with the command `/bin/bash /usr/local/nagios/libexec/host_config.sh` and the options "-i" to 
insert a new host profile, "-d" to delete an existing profile, and "-a" to list all currently defined profiles.
* The final method is using the profile.csv file to programmatically add in all your host profiles at once. 
This is most useful if something went wrong with the Nagios.host.java.config.ser file and it
needs to be setup again but you still have the hosts/services defined in Nagios XI.
    * This can be done by unzipping the ibmiservice.zip file downloaded from GitHub.
    * Opening the profile.csv file found inside and adding in all the hosts you'd like to define.
    * Zip the file again, ensuring it has the exact same folder structure as the original. 
Otherwise it will not import into Nagios XI. It should be ibmiservice.zip > ibmiservice > (main directory).
    * In command line on your Nagios XI system navigate to "/usr/local/nagios/var" and rename/remove "profile.csv" if it exists.
    * Upload the zip file as you normally would through the Nagios XI GUI. 

  At this point every host profile defined in the profile.csv file should be written into the 
Nagios.host.java.config.ser config file. Note, like the CLI method, this will ONLY affect the host profiles.
Nothing will change for the defined hosts/services, although now they will be using these new profiles to run.
Once the above steps are taken and profile.csv contains the profiles you'd like to define, you can run 
`/bin/bash /usr/local/nagios/libexec/ibmi_profile_init.sh` to upload all the defined host profiles into the
config file again.

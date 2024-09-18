<?php
//
// IBM i service Config Wizard
// Copyright (c) 2010-2017 Nagios Enterprises, LLC. All rights reserved.
//

include_once(dirname(__FILE__) . '/../configwizardhelper.inc.php');

ibm_i_service_configwizard_init();

function ibm_i_service_configwizard_init()
{
    $name = "ibm-i-service";
    $args = array(
        CONFIGWIZARD_NAME => $name,
        CONFIGWIZARD_VERSION => "1.0.3",
        CONFIGWIZARD_TYPE => CONFIGWIZARD_TYPE_MONITORING,
        CONFIGWIZARD_DESCRIPTION => _("Monitor an IBM i server"),
        CONFIGWIZARD_DISPLAYTITLE => "IBM i service",
        CONFIGWIZARD_FUNCTION => "ibm_i_service_configwizard_func",
        CONFIGWIZARD_PREVIEWIMAGE => "ibmi.png",
    );
    register_configwizard($name, $args);
}


/**
 * @param string $mode
 * @param null   $inargs
 * @param        $outargs
 * @param        $result
 *
 * @return string
 */
function ibm_i_service_configwizard_func($mode = "", $inargs = null, &$outargs, &$result)
{
    $wizard_name = "ibm-i-service";

    // Initialize return code and output
    $result = 0;
    $output = "";

    // Initialize output args - pass back the same data we got
    $outargs[CONFIGWIZARD_PASSBACK_DATA] = $inargs;

    switch ($mode) {
        case CONFIGWIZARD_MODE_GETSTAGE1HTML:
            $address = grab_array_var($inargs, "ip_address");
            $address = nagiosccm_replace_user_macros($address);

            $output = '';

            $output .= '
            <p style="margin: 10px 0 20px 0;">' . _('<b>NOTE: </b>The Nagios plugin for IBM i requires JAVA to be installed on the Nagios Server.') 
            . '
            </p>
            <h5 class="ul">'._('Server Information').'</h5>
            <table class="table table-condensed table-no-border table-auto-width">
                <tr>
                    <td class="vt">
                        <label for="ip_address">'._('IP Address').':</label>
                    </td>
                    <td>
                        <input type="text" size="40" name="ip_address" id="ip_address" value="'.encode_form_val($address).'" class="form-control">
                        <div class="subtext">'._("The IP address of the server you'd like to monitor").'.</div>
                    </td>
                </tr>
            </table>';
            
            break;

        case CONFIGWIZARD_MODE_VALIDATESTAGE1DATA:
            $address = grab_array_var($inargs, "ip_address", "");
            // Check for errors
            $errors = 0;
            $errmsg = array();

            $return = exec('export DYLD_LIBRARY_PATH="";java -version 2>&1');
            if(strpos($return, 'command not found') !== false) {
                $errmsg[$errors++] = _("No JAVA detected. <br>Please open a terminal to the command line of your Nagios XI installation and install JAVA.<br>For CentOS/RHEL systems please run: yum install -y java-1.8.0-openjdk<br>For Debian-based systems please run: apt-get install -y default-jre");
            }

            if (empty($address)) {
                $errmsg[$errors++] = _("No address specified.");
            }

            if ($errors > 0) {
                $outargs[CONFIGWIZARD_ERROR_MESSAGES] = $errmsg;
                $result = 1;
            }
           
            break;

        case CONFIGWIZARD_MODE_GETSTAGE2HTML:

            // Get variables that were passed to us
            $cust_sql = "";
            $cust_sql_serial = grab_array_var($inargs, "cust_sql_serial");
            if ($cust_sql_serial != "") {
                $cust_sql = unserialize(base64_decode($cust_sql_serial));
            } else {
                $cust_sql = grab_array_var($inargs, "cust_sql");
            }

            $cust_services = array();
            $cust_services_serial = grab_array_var($inargs, "cust_services_serial", "");
            if ($cust_services_serial != "") {
                $cust_services = unserialize(base64_decode($cust_services_serial));
            }
            
            $address = grab_array_var($inargs, "ip_address", "");
            $ha = @gethostbyaddr($address);
            if (empty($ha)) {
                $ha = $address;
            }
            $hostname = grab_array_var($inargs, "hostname", $ha);
            $hostname = nagiosccm_replace_user_macros($hostname);
            $usr_profile = grab_array_var($inargs, "usr_profile");
            $usr_password = grab_array_var($inargs, "usr_password");
            $sst_profile = grab_array_var($inargs, "sst_profile");
            $sst_password = grab_array_var($inargs, "sst_password");
            $ssl = grab_array_var($inargs, "ssl");
            $server_type = grab_array_var($inargs, "server_type");

            $services = "";
            $services_serial = grab_array_var($inargs, "services_serial", "");
            if ($services_serial != "") {
                $services = unserialize(base64_decode($services_serial));
            }
            if (!is_array($services)) {
                $services_default = array();
                $services_default[0] = "off";
                $services_default[1] = "off";
                $services_default[2] = "off";
                $services_default[3] = "off";
                $services_default[4] = "off";
                $services_default[5] = "off";
                $services_default[6] = "off";
                $services_default[7] = "off";
                $services_default[8] = "off";
                $services_default[9] = "off";
                $services_default[10] = "off";
                $services_default[11] = "off";
                $services_default[12] = "off";
                $services_default[13] = "off";
                $services_default[14] = "off";
                $services_default[15] = "off";
                $services_default[16] = "off";
                $services_default[17] = "off";
                $services_default[18] = "off";
                $services_default[19] = "off";
                $services = grab_array_var($inargs, "services", $services_default);
            }

            $serviceargs = "";
            $serviceargs_serial = grab_array_var($inargs, "serviceargs_serial", "");
            if ($serviceargs_serial != "") {
                $serviceargs = unserialize(base64_decode($serviceargs_serial));
            }
            if (!is_array($serviceargs)) {
                $serviceargs_default = array(
                    array()
                );
                for ($x = 0; $x < 20; $x++) {
                    if ($x == 0) {
                        $serviceargs_default[$x]['description'] = 'CPU Utilization';
                        $serviceargs_default[$x]['metric'] = 'CPU';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'CPU';
                        $serviceargs_default[$x]['warning'] = 80;
                        $serviceargs_default[$x]['critical'] = 90;
                    } else if ($x == 1) {
                        $serviceargs_default[$x]['description'] = 'CPU Overload Jobs';
                        $serviceargs_default[$x]['metric'] = 'CPUOverloadJobs';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'CPU Overload Jobs';
                        $serviceargs_default[$x]['warning'] = 80;
                        $serviceargs_default[$x]['critical'] = 90;
                    } else if ($x == 2) {
                        $serviceargs_default[$x]['description'] = 'Specific Job CPU';
                        $serviceargs_default[$x]['metric'] = 'SpecificJobCPU';
                        $serviceargs_default[$x]['args'] = '-j ADMIN2';
                        $serviceargs_default[$x]['name'] = 'Specific Job CPU';
                        $serviceargs_default[$x]['warning'] = 80;
                        $serviceargs_default[$x]['critical'] = 90;
                    } else if ($x == 3) {
                        $serviceargs_default[$x]['description'] = 'ASP Usage';
                        $serviceargs_default[$x]['metric'] = 'ASPUsage';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'ASP Usage';
                        $serviceargs_default[$x]['warning'] = 80;
                        $serviceargs_default[$x]['critical'] = 90;
                    } else if ($x == 4) {
                        $serviceargs_default[$x]['description'] = 'IASP Usage';
                        $serviceargs_default[$x]['metric'] = 'IASPUsage';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'IASP Usage';
                        $serviceargs_default[$x]['warning'] = 80;
                        $serviceargs_default[$x]['critical'] = 90;
                    } else if ($x == 5) {
                        $serviceargs_default[$x]['description'] = 'Disk Usage';
                        $serviceargs_default[$x]['metric'] = 'DiskUsage';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'Disk Usage';
                        $serviceargs_default[$x]['warning'] = 80;
                        $serviceargs_default[$x]['critical'] = 90;
                    } else if ($x == 6) {
                        $serviceargs_default[$x]['description'] = 'Number of logon users';
                        $serviceargs_default[$x]['metric'] = 'CurSignOnUsers';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'Number of logon users';
                        $serviceargs_default[$x]['warning'] = 10;
                        $serviceargs_default[$x]['critical'] = 20;
                    } else if ($x == 7) {
                        $serviceargs_default[$x]['description'] = 'Page Faults';
                        $serviceargs_default[$x]['metric'] = 'PageFaults';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'Page Faults';
                        $serviceargs_default[$x]['warning'] = 10;
                        $serviceargs_default[$x]['critical'] = 20;
                    } else if ($x == 8) {
                        $serviceargs_default[$x]['description'] = 'Subsystem Jobs Information';
                        $serviceargs_default[$x]['metric'] = 'SubsystemJobs';
                        $serviceargs_default[$x]['args'] = '-s qhttpsvr';
                        $serviceargs_default[$x]['name'] = 'Subsystem Jobs Information';
                        $serviceargs_default[$x]['warning'] = 20;
                        $serviceargs_default[$x]['critical'] = 30;
                    } else if ($x == 9) {
                        $serviceargs_default[$x]['description'] = 'Number of Active Jobs';
                        $serviceargs_default[$x]['metric'] = 'ActiveJobs';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'Number of Active Jobs';
                        $serviceargs_default[$x]['warning'] = 200;
                        $serviceargs_default[$x]['critical'] = 300;
                    } else if ($x == 10) {
                        $serviceargs_default[$x]['description'] = 'Longest Running SQL';
                        $serviceargs_default[$x]['metric'] = 'LongRunSQL';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'Longest Running SQL';
                        $serviceargs_default[$x]['warning'] = 20;
                        $serviceargs_default[$x]['critical'] = 30;
                    } else if ($x == 11) {
                        $serviceargs_default[$x]['description'] = 'Message';
                        $serviceargs_default[$x]['metric'] = 'Message';
                        $serviceargs_default[$x]['args'] = '-lib QSYS -name QSYSOPR -ty INQUIRY,ESCAPE,REPLY';
                        $serviceargs_default[$x]['name'] = 'Message';
                        $serviceargs_default[$x]['warning'] = 20;
                        $serviceargs_default[$x]['critical'] = 30;
                    } else if ($x == 12) {
                        $serviceargs_default[$x]['description'] = 'Basic Information';
                        $serviceargs_default[$x]['metric'] = 'BasicInfo';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'Basic Information';
                    } else if ($x == 13) {
                        $serviceargs_default[$x]['description'] = 'Specific Message';
                        $serviceargs_default[$x]['metric'] = 'SpecificMessage';
                        $serviceargs_default[$x]['args'] = '-i CPF*';
                        $serviceargs_default[$x]['name'] = 'Specific Message';
                    } else if ($x == 14) {
                        $serviceargs_default[$x]['description'] = 'Top N Temporary Storage Jobs';
                        $serviceargs_default[$x]['metric'] = 'TempStorageJobs';
                        $serviceargs_default[$x]['args'] = '-n 10';
                        $serviceargs_default[$x]['name'] = 'Top N Temporary Storage Jobs';
                    } else if ($x == 15) {
                        $serviceargs_default[$x]['description'] = 'Disk Status';
                        $serviceargs_default[$x]['metric'] = 'DiskConfig';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'Disk Status';
                    } else if ($x == 16) {
                        $serviceargs_default[$x]['description'] = 'Management Console Version';
                        $serviceargs_default[$x]['metric'] = 'ManagementConsoleVersion';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'Management Console Version';
                    } else if ($x == 17) {
                        $serviceargs_default[$x]['description'] = 'HMC Managed Systems';
                        $serviceargs_default[$x]['metric'] = 'Systems';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'Systems';
                    } else if ($x == 18) {
                        $serviceargs_default[$x]['description'] = 'HMC Managed Partitions';
                        $serviceargs_default[$x]['metric'] = 'Partitions';
                        $serviceargs_default[$x]['args'] = '';
                        $serviceargs_default[$x]['name'] = 'Partitions';
                    } else if ($x == 19) {
                        $serviceargs_default[$x]['description'] = 'Check the Partitions Status from IBM i';
                        $serviceargs_default[$x]['metric'] = 'SpecificPartition';
                        $serviceargs_default[$x]['args'] = '-FOI ReferenceCode -PARNAME XXXXXXXX -EXPVAL 00000000';
                        $serviceargs_default[$x]['name'] = 'Specific Partition';
                    }
                }

                $serviceargs = grab_array_var($inargs, "serviceargs", $serviceargs_default);
            }

            $output = '
<input type="hidden" name="ip_address" value="' . encode_form_val($address) . '">
<script type="text/javascript">
    $(function(){
        $("#server_type").change(function(e){
        console.log("enter change function");
        var type = $("#server_type").val();

        if(type == "IBMi") {
            //IBMi service plugin
            for(var i=0; i<16; i++) {
                var id = "chkbox" + i;
                $("#"+id).removeAttr("disabled");
            }
            //HMC plugin
            for(var i=16; i<=19; i++) {
                var id = "chkbox" + i;
                $("#"+id).attr("disabled",true);
            }
            //specific partition plugin
            // $("#chkbox19").removeAttr("disabled");
            //custom sql plugin
            var counts = $("#custplugin").find("tr").length;
            for(var i=0; i<counts; i++) {
                var id = "custchkbox" + i;
                $("#"+id).removeAttr("disabled");
            }
        } else if(type == "HMC") {
            //IBMi service plugin
            for(var i=0; i<16; i++) {
                var id = "chkbox" + i;
                $("#"+id).attr("disabled",true);
            }
            //HMC plugin
            for(var i=16; i<=19; i++) {
                var id = "chkbox" + i;
                $("#"+id).removeAttr("disabled");
            }
            //specific partition plugin
            // $("#chkbox19").attr("disabled",true);
            //custom sql plugin
            var counts = $("#custplugin").find("tr").length;
            for(var i=0; i<counts; i++) {
                var id = "custchkbox" + i;
                $("#"+id).attr("disabled",true);
            }
        }
　　});　　
});
</script> 

<h5 class="ul">' . _('Server Details') . '</h5>
<table class="table table-condensed table-no-border table-auto-width">
    <tr>
        <td>
            <label for="ip_address">' . _('IP Address') . ':</label>
        </td>
        <td>
            <input type="text" size="40" name="ip_address" id="ip_address" value="' . encode_form_val($address) . '" class="form-control" disabled>
        </td>
    </tr>
    <tr>
        <td class="vt">
            <label for="hostname">' . _('Host Name') . ':</label>
        </td>
        <td>
            <input type="text" size="20" name="hostname" id="hostname" value="' . encode_form_val($hostname) . '" class="form-control">
            <div class="subtext">' . _("The name you'd like to have associated with this host") . '.</div>
        </td>
        <td class="vt">
            <label for="server_type">'._("Server Type").':</label>
        </td>
        <td>
            <select name="server_type" id="server_type" class="form-control">
                <option value="IBMi" ' . is_selected($server_type, "IBMi") . '>' . _('IBM i (Default)') . '</option>
                <option value="HMC" ' . is_selected($server_type, "HMC") . '>' . _('HMC') . '</option>
            </select>
            <div class="subtext">' . _('Determines whether or not data between the Nagios XI server and IBM i server is encrypted') . '.</div>
        </td>
    </tr>
    <tr>
        <td valign="top">
            <label>' . _('User Profile') . ':</label>
        </td>
        <td>
            <input type="text" size="20" name="usr_profile" id="usr_profile" value="' . encode_form_val($usr_profile) . '" class="form-control">
            <div class="subtext">' . _("Specify the user profile of IBM i server") . '.</div>
        </td>
        <td valign="top">
            <label>' . _('Password') . ':</label>
        </td>
        <td>
            <input type="password" size="20" name="usr_password" id="usr_password" value="' . encode_form_val($usr_password) . '" class="form-control">
            <div class="subtext">' . _("Specify the passowrd of the user profile") . '.</div>
        </td>
    </tr>
    <tr>
        <td class="vt">
            <label for="ssl">'._("SSL Encryption").':</label>
        </td>
        <td>
            <select name="ssl" id="ssl" class="form-control">
                <option value="off" ' . is_selected($ssl, "off") . '>' . _('Disabled (Default)') . '</option>
                <option value="on" ' . is_selected($ssl, "on") . '>' . _('Enabled') . '</option>
            </select>
            <div class="subtext">' . _('Determines whether or not data between the Nagios XI server and IBM i server is encrypted') . '.</div>
        </td>
    </tr> 
</table>
    
<h5 class="ul">' . _('IBM i Plugin Service') . '</h5>
<p>' . _('Specify the commands that should be monitored on the server. Multiple command arguments should be separated with a space.')  . '</p>
<table class="table table-condensed table-no-border table-auto-width" style="margin: 0;">
    <tr>
        <th></th>
        <th>' . _('Display Name') . '</th>
        <th>' . _('Monitored Metric') . '</th>
        <th>' . _('Command Args') . '</th>
    </tr>';

            for ($x = 0; $x < 16; $x++) {

                $description = encode_form_val($serviceargs[$x]['description']);
                $commandargs = encode_form_val($serviceargs[$x]['args']);
                $commandname = encode_form_val($serviceargs[$x]['name']);
                $metric = encode_form_val($serviceargs[$x]['metric']);
                $is_service_checked = (isset($services[$x]) ? is_checked($services[$x]) : '');

                $output .= '
                <tr>
                    <td><input id="chkbox' . $x . '" type="checkbox" class="checkbox" name="services[' . $x . ']" ' . $is_service_checked . ' class="form-control"></td>
                    <td><input type="text" size="25" name="serviceargs[' . $x . '][name]" value="' . $commandname . '" class="form-control"></td>
                    <td><input type="text" size="35" name="serviceargs[' . $x . '][description]" value="' . $description . '" class="form-control" readonly></td>
                    <td><input type="text" size="50" name="serviceargs[' . $x . '][args]" value="' . $commandargs . '" class="form-control">';
                if($metric == 'SpecificJobCPU') {
                    $output .= '<div class="subtext">' . _('Specify the job name by parameter -j') . '.</div></td>';
                } else if($metric == 'SubsystemJobs') {
                    $output .= '<div class="subtext">' . _('Specify the subsystem by parameter -s') . '.</div></td>';
                } else if($metric == 'Message') {
                    $output .= '<div class="subtext">' . _('-lib for library; -name for message queue; -ty for message type') . '.</div></td>';
                } else if($metric == 'SpecificMessage') {
                    $output .= '<div class="subtext">' . _('Specify the message id by parameter -i, such as "CPF*" or "CPF0001,CPF0002,CPF0003"') . '.</div></td>';
                } else if($metric == 'TempStorageJobs') {
                    $output .= '<div class="subtext">' . _('Specify the number of jobs by parameter -n') . '.</div></td>';
                } else {
                    $output .= '</td>';
                }
                    $output .= '
                    <td><input type="hidden" name="serviceargs[' . $x . '][metric]" value="' . $metric . '"></td>';
                if($x <= 5) {
                    $output .= '
                    <td><label><img src="'.theme_image('error.png').'" class="tt-bind" title="'._('Warning Threshold').'"></label> <input type="text" size="2" name="serviceargs['.$x.'][warning]" value="' . encode_form_val($serviceargs[$x]["warning"]) . '" class="form-control condensed">% &nbsp;<label><img src="'.theme_image('critical_small.png').'" class="tt-bind" title="'._('Critical Threshold').'"></label> <input type="text" size="2" name="serviceargs['.$x.'][critical]" value="' . encode_form_val($serviceargs[$x]["critical"]) . '" class="form-control condensed"> %</td>';
                } else if($x <= 11) {
                    $output .= '
                    <td><label><img src="'.theme_image('error.png').'" class="tt-bind" title="'._('Warning Threshold').'"></label> <input type="text" size="2" name="serviceargs['.$x.'][warning]" value="' . encode_form_val($serviceargs[$x]["warning"]) . '" class="form-control condensed">&nbsp &nbsp;<label><img src="'.theme_image('critical_small.png').'" class="tt-bind" title="'._('Critical Threshold').'"></label> <input type="text" size="2" name="serviceargs['.$x.'][critical]" value="' . encode_form_val($serviceargs[$x]["critical"]) . '" class="form-control condensed"></td>';
                }
                $output .= '</tr>';
            }

            $output .= '</table>
            <p>If you click the checkbox for <b>Disk Status</b> above, you must specify the SST Profile in the fields below. The password level for the SST profile in Dedicated Service Tools (DST) must be set to level 2.<br>
            To change password level, go to STRSST-> Option 8: Work With Service Tools Server Security and Devices -> Option 4: Change service tools password level.<br>
            Once the level is changed, the password itself must be changed for the new level to take effect.</p>
            <div>
                <tr>
                    <td valign="top">
                        <label>' . _('SST Profile') . ':</label>
                    </td>
                    <td>
                        <input type="text" size="20" name="sst_profile" id="sst_profile" value="' . encode_form_val($sst_profile) . '" class="form-control">
                    </td>
                    <td valign="top">
                        <label>' . _('SST Password') . ':</label>
                    </td>
                    <td>
                        <input type="password" size="20" name="sst_password" id="sst_password" value="' . encode_form_val($sst_password) . '" class="form-control">
                    </td>
                </tr>
            </div>';
            $output .= '<br>
<h5 class="ul">' . _('HMC Plugin Service') . '</h5>
<table class="table table-condensed table-no-border table-auto-width" style="margin: 0;">
    <tr>
        <th></th>
        <th>' . _('Display Name') . '</th>
        <th>' . _('Monitored Metric') . '</th>
        <th>' . _('Command Args') . '</th>
    </tr>';
            for ($x = 16; $x < 20; $x++) {

                $description = encode_form_val($serviceargs[$x]['description']);
                $commandargs = encode_form_val($serviceargs[$x]['args']);
                $commandname = encode_form_val($serviceargs[$x]['name']);
                $metric = encode_form_val($serviceargs[$x]['metric']);
                $is_service_checked = (isset($services[$x]) ? is_checked($services[$x]) : '');

                $output .= '
    <tr>';
                if($x != 19) {
                    $output .= '
        <td><input id="chkbox' . $x . '" type="checkbox" class="checkbox" name="services[' . $x . ']" ' . $is_service_checked . ' class="form-control" disabled="disabled"></td>';
                } else {
                    $output .= '
        <td><input id="chkbox' . $x . '" type="checkbox" class="checkbox" name="services[' . $x . ']" ' . $is_service_checked . ' class="form-control"></td>';
                }
                $output .= '
        <td><input type="text" size="25" name="serviceargs[' . $x . '][name]" value="' . $commandname . '" class="form-control"></td>
        <td><input type="text" size="35" name="serviceargs[' . $x . '][description]" value="' . $description . '" class="form-control" readonly></td>
        <td><input type="text" size="100" name="serviceargs[' . $x . '][args]" value="' . $commandargs . '" class="form-control"></td>
        <td><input type="hidden" name="serviceargs[' . $x . '][metric]" value="' . $metric . '"></td>
    </tr>';
            }
            $output .= '
    <div class="subtext">Following plugin are used to monitor HMC servers. There are some parameters used to configure the certificate store and HMC rest service. If you specify the parameter plugins will be called by your configuration. If not plugins will use the default value.<br>
        &nbsp;&nbsp;&nbsp;&nbsp;-KSPASS - password of the default certificate keystore. Default value is "changeit".<br>
        &nbsp;&nbsp;&nbsp;&nbsp;-PORT - The port number of HMC rest service. Default value is "12443".
    </div>
</table>
<p>For the plugin Specific Partition you can check the MHC patition status from IBM i server. And following parameters are required.<br>
&nbsp;&nbsp;&nbsp;&nbsp;-FOI - the field of interest. It specifies the field you want to monitor.<br>
&nbsp;&nbsp;&nbsp;&nbsp;-PARNAME - Partition name. The name of the partition you want to check.<br>
&nbsp;&nbsp;&nbsp;&nbsp;-EXPVAL - Expect value. The expect value of the interested field. It will compare the value returned from server and the expect value to report the status.</p>';



            $xml = new XMLReader ();
            $xml->open('/usr/local/nagios/etc/objects/CustomSQL.xml');
            $count = -1;
            $name = '';
            $cust_sql = array();
            $xmlTag = array(
                'common-name',
                'type',
                'sql-command',
                'warning',
                'critical'
            );
            while($xml->read()) {
                $node = $xml->name;
                if ($xml->nodeType == XMLREADER::ELEMENT) {
                    if($node == 'func') {
                        $count++;
                        $cust_sql[$count]["id"] = $xml->getAttribute("id");
                    } else if(in_array($node, $xmlTag)) {
                        $name = $node;
                    }
                } else if($xml->nodeType == XMLREADER::TEXT || $xml->nodeType == XMLREADER::CDATA) {
                    if(in_array($name, $xmlTag)) {
                        $cust_sql[$count][$name] = $xml->value;
                    }
                }
            }

            $output .= '<br>
            <h5 class="ul">' . _('Custom Plugin Service') . '</h5>
<table id="custplugin" class="table table-condensed table-no-border table-auto-width" style="margin: 0;">
    <tr>
        <th></th>
        <th>' . _('Function Id') . '</th>
        <th>' . _('Description') . '</th>
        <th>' . _('Type') . '</th>
        <th>' . _('Thresholds') . '</th>
        <th>' . _('SQL Command') . '</th>
    </tr>';
            for ($x = 0; $x < count($cust_sql); $x++) {
                $is_cust_checked = (isset($cust_services[$x]) ? is_checked($cust_services[$x]) : '');
                $output .= '
                <tr>
                    <td><input id="custchkbox' . $x . '" type="checkbox" class="checkbox" name="cust_services[' . $x . ']" ' . $is_cust_checked . ' class="form-control"></td>
                    <td><input type="text" size="30" name="cust_sql[' . $x . '][id]" id="function_id" value="' . encode_form_val($cust_sql[$x]["id"]) . '" class="form-control" readonly></td>
                    <td><input type="text" size="30" name="cust_sql[' . $x . '][common-name]" id="common-name" value="' . encode_form_val($cust_sql[$x]["common-name"]) . '" class="form-control" readonly></td>
                    <td><input type="text" size="30" name="cust_sql[' . $x . '][type]" id="type" value="' . encode_form_val($cust_sql[$x]["type"]) . '" class="form-control" readonly></td>
                    <td><label><img src="'.theme_image('error.png').'" class="tt-bind" title="'._('Warning Threshold').'"></label>
                        <input type="text" size="2" name="cust_sql['.$x.'][warning]" value="' . encode_form_val($cust_sql[$x]["warning"]) . '" class="form-control condensed" readonly>&nbsp;
                        <label><img src="'.theme_image('critical_small.png').'" class="tt-bind" title="'._('Critical Threshold').'"></label>
                        <input type="text" size="2" name="cust_sql['.$x.'][critical]" value="' . encode_form_val($cust_sql[$x]["critical"]) . '" class="form-control condensed" readonly>
                    </td>
                    <td><input type="text" size="100" name="cust_sql[' . $x . '][sql-command]" id="sql-command" value="' . encode_form_val($cust_sql[$x]["sql-command"]) . '" class="form-control" readonly></td>
                </tr>';
            }
            $output .= '<div class="subtext">' . _('You can edit these custom SQL commands, and also add new ones, in the <b>IBM i Custom SQL Wizard</b>. Changes made there will be reflected here the next time you run this Wizard.') . '.</div></table>
            <div style="height: 20px;"></div>';
            
            break;

        case CONFIGWIZARD_MODE_VALIDATESTAGE2DATA:

            // Get variables that were passed to us
            $address = grab_array_var($inargs, "ip_address");
            $hostname = grab_array_var($inargs, "hostname");
            $server_type = grab_array_var($inargs, "server_type");
            $usr_profile = grab_array_var($inargs, "usr_profile");
            $usr_password = grab_array_var($inargs, "usr_password");
            $sst_profile = grab_array_var($inargs, "sst_profile");
            $sst_password = grab_array_var($inargs, "sst_password");
            $services = grab_array_var($inargs, "services");
            $cust_services = grab_array_var($inargs, "cust_services");
            // Check for errors
            $errors = 0;
            $errmsg = array();
            if (is_valid_host_name($hostname) == false) {
                $errmsg[$errors++] = "Invalid host name.";
            }

            if (have_value($usr_profile) == false || have_value($usr_password) == false) {
                $errmsg[$errors++] = _("User profile or password not specified.");
            } else {
                if($server_type == "IBMi") {
                    $command = 'export DYLD_LIBRARY_PATH="";java -cp /usr/local/nagios/libexec/jt400.jar:/usr/local/nagios/libexec/nagios4i.jar com.ibm.nagios.config.HostConfig -i host ' . escapeshellarg($address) . ' ' . escapeshellarg($usr_profile) . ' ' . escapeshellarg($usr_password);
                } else if($server_type == "HMC") {
                    $command = 'export DYLD_LIBRARY_PATH="";java -cp /usr/local/nagios/libexec/jt400.jar:/usr/local/nagios/libexec/nagios4i.jar com.ibm.nagios.config.HostConfig -i hmc ' . escapeshellarg($address) . ' ' . escapeshellarg($usr_profile) . ' ' . escapeshellarg($usr_password);
                }
                exec($command, $ret);
                if (strpos(implode(",", $ret), "failed") !== false) {
                    $errmsg[$errors++] = _("Register profile error:");
                    for($i=0; $i<count($ret); $i++) {
                        $errmsg[$errors++] = $ret[$i];
                    }
                }
            }

            if((isset($services[15]) ? is_checked($services[15]) : '')) {
                if (have_value($sst_profile) == false || have_value($sst_password) == false) {
                    $errmsg[$errors++] = _("Disk Status is selected. SST profile and password must be specified.");
                } else {
                    $command = 'export DYLD_LIBRARY_PATH="";java -cp /usr/local/nagios/libexec/jt400.jar:/usr/local/nagios/libexec/nagios4i.jar com.ibm.nagios.config.HostConfig -i sst ' . escapeshellarg($address) . ' ' . escapeshellarg($sst_profile) . ' ' . escapeshellarg($sst_password);
                    exec($command);
                }
            }

            if ($errors > 0) {
                $outargs[CONFIGWIZARD_ERROR_MESSAGES] = $errmsg;
                $result = 1;
            }
            break;

        case CONFIGWIZARD_MODE_GETSTAGE3HTML:

            // Get variables that were passed to us
            $address = grab_array_var($inargs, "ip_address");
            $hostname = grab_array_var($inargs, "hostname");
            $ssl = grab_array_var($inargs, "ssl", "off");
            $server_type = grab_array_var($inargs, "server_type");
            $usr_profile = grab_array_var($inargs, "usr_profile");
            $usr_password = grab_array_var($inargs, "usr_password");
            $sst_profile = grab_array_var($inargs, "sst_profile");
            $sst_password = grab_array_var($inargs, "sst_password");

            $services = "";
            $services_serial = grab_array_var($inargs, "services_serial");
            if ($services_serial != "") {
                $services = unserialize(base64_decode($services_serial));
            } else {
                $services = grab_array_var($inargs, "services");
            }

            $cust_sql = "";
            $cust_sql_serial = grab_array_var($inargs, "cust_sql_serial");
            if ($cust_sql_serial != "") {
                $cust_sql = unserialize(base64_decode($cust_sql_serial));
            } else {
                $cust_sql = grab_array_var($inargs, "cust_sql");
            }

            $cust_services = "";
            $cust_services_serial = grab_array_var($inargs, "cust_services_serial");
            if ($cust_services_serial != "") {
                $cust_services = unserialize(base64_decode($cust_services_serial));
            } else {
                $cust_services = grab_array_var($inargs, "cust_services");
            }

            $serviceargs = "";
            $serviceargs_serial = grab_array_var($inargs, "serviceargs_serial");
            if ($serviceargs_serial != "") {
                $serviceargs = unserialize(base64_decode($serviceargs_serial));
            } else {
                $serviceargs = grab_array_var($inargs, "serviceargs");
            }

            $output = '
            <input type="hidden" name="ip_address" value="' . encode_form_val($address) . '">
            <input type="hidden" name="hostname" value="' . encode_form_val($hostname) . '">
            <input type="hidden" name="usr_profile" value="' . encode_form_val($usr_profile) . '">
            <input type="hidden" name="usr_password" value="' . encode_form_val($usr_password) . '">
            <input type="hidden" name="sst_profile" value="' . encode_form_val($sst_profile) . '">
            <input type="hidden" name="sst_password" value="' . encode_form_val($sst_password) . '">
            <input type="hidden" name="ssl" value="' . encode_form_val($ssl) . '">
            <input type="hidden" name="services_serial" value="' . base64_encode(serialize($services)) . '">
            <input type="hidden" name="cust_sql_serial" value="' . base64_encode(serialize($cust_sql)) . '">
            <input type="hidden" name="cust_services_serial" value="' . base64_encode(serialize($cust_services)) . '">
            <input type="hidden" name="serviceargs_serial" value="' . base64_encode(serialize($serviceargs)) . '">';
            break;

        case CONFIGWIZARD_MODE_VALIDATESTAGE3DATA:

            // Get variables that were passed to us
            
            break;

        case CONFIGWIZARD_MODE_GETFINALSTAGEHTML:

            break;

        case CONFIGWIZARD_MODE_GETOBJECTS:

            $hostname = grab_array_var($inargs, "hostname", "");
            $address = grab_array_var($inargs, "ip_address", "");
            $osdistro = grab_array_var($inargs, "osdistro", "");
            $ssl = grab_array_var($inargs, "ssl", "off");
            $hostaddress = $address;

            $services_serial = grab_array_var($inargs, "services_serial", "");
            $serviceargs_serial = grab_array_var($inargs, "serviceargs_serial", "");
            $cust_sql_serial = grab_array_var($inargs, "cust_sql_serial", "");
            $cust_services_serial = grab_array_var($inargs, "cust_services_serial", "");

            $services = unserialize(base64_decode($services_serial));
            $serviceargs = unserialize(base64_decode($serviceargs_serial));
            $cust_sql = unserialize(base64_decode($cust_sql_serial));
            $cust_services = unserialize(base64_decode($cust_services_serial));

            // Save data for later use in re-entrance
            $meta_arr = array();
            $meta_arr["hostname"] = $hostname;
            $meta_arr["ip_address"] = $address;
            $meta_arr["services"] = $services;
            $meta_arr["serviceargs"] = $serviceargs;
            save_configwizard_object_meta($wizard_name, $hostname, "", $meta_arr);

            $objs = array();

            if (!host_exists($hostname)) {
                $objs[] = array(
                    "type" => OBJECTTYPE_HOST,
                    "use" => "IBM-i",
                    "host_name" => $hostname,
                    "address" => $hostaddress,
                    "icon_image" => $icon,
                    "statusmap_image" => $icon,
                    "_xiwizard" => $wizard_name,
                );
            }

            // Optional non-SSL args to add
            $sslargs = "";
            if ($ssl == "on") {
                $sslargs .= "y";
            } else if ($ssl == "off") {
                $sslargs .= "n";
            }

            // See which services we should monitor

            for ($x = 0; $x < 16; $x++) {
                if(!(isset($services[$x]) ? is_checked($services[$x]) : ''))
                    continue;

                $displayname = $serviceargs[$x]["name"];
                $pname = $serviceargs[$x]["metric"];
                $pargs = $serviceargs[$x]["args"];
                $pdesc = $serviceargs[$x]["description"];


                $checkcommand = "check_ibmi!" . $pname . "!" . $sslargs  . "!" . $serviceargs[$x]["warning"] . "!" . $serviceargs[$x]["critical"] . "!" . $pargs;

                $objs[] = array(
                    "type" => OBJECTTYPE_SERVICE,
                    "host_name" => $hostname,
                    "service_description" => $pdesc,
                    "use" => "generic-service",
                    "check_command" => $checkcommand,
                    "_xiwizard" => $wizard_name,
                );
            }

            for ($x = 16; $x < 20; $x++) {
                if(!(isset($services[$x]) ? is_checked($services[$x]) : ''))
                    continue;

                $displayname = $serviceargs[$x]["name"];
                $pname = $serviceargs[$x]["metric"];
                $pargs = $serviceargs[$x]["args"];
                $pdesc = $serviceargs[$x]["description"];


                $checkcommand = "check_hmc!" . $sslargs . "!" . $pname . "!" . $pargs;

                $objs[] = array(
                    "type" => OBJECTTYPE_SERVICE,
                    "host_name" => $hostname,
                    "service_description" => $pdesc,
                    "use" => "generic-service",
                    "check_command" => $checkcommand,
                    "_xiwizard" => $wizard_name,
                );
            }

            for ($x = 0; $x < count($cust_sql); $x++) {
                if(!(isset($cust_services[$x]) ? is_checked($cust_services[$x]) : ''))
                    continue;

                $function_id = $cust_sql[$x]["id"];
                $pdesc = $cust_sql[$x]["common-name"];
                $warning = $cust_sql[$x]["warning"];
                $critical = $cust_sql[$x]["critical"];


                $checkcommand = "check_ibmi_cust!" . $sslargs . "!" . $function_id . "!" . $cust_sql[$x]["warning"] . "!" . $cust_sql[$x]["critical"];

                $objs[] = array(
                    "type" => OBJECTTYPE_SERVICE,
                    "host_name" => $hostname,
                    "service_description" => $pdesc,
                    "use" => "generic-service",
                    "check_command" => $checkcommand,
                    "_xiwizard" => $wizard_name,
                );
            }

            // Return the object definitions to the wizard
            $outargs[CONFIGWIZARD_NAGIOS_OBJECTS] = $objs;

            break;

        default:
            break;
    }

    return $output;
}

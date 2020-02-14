<?php
//
// IBM i command Config Wizard
// Copyright (c) 2010-2017 Nagios Enterprises, LLC. All rights reserved.
//

include_once(dirname(__FILE__) . '/../configwizardhelper.inc.php');

ibm_i_customsql_configwizard_init();

function ibm_i_customsql_configwizard_init()
{
    $name = "ibm-i-customsql";
    $args = array(
        CONFIGWIZARD_NAME => $name,
        CONFIGWIZARD_VERSION => "1.0.0",
        CONFIGWIZARD_TYPE => CONFIGWIZARD_TYPE_MONITORING,
        CONFIGWIZARD_DESCRIPTION => _("Configure Custom SQL commands for IBM i monitoring."),
        CONFIGWIZARD_DISPLAYTITLE => "IBM i Custom SQL",
        CONFIGWIZARD_FUNCTION => "ibm_i_customsql_configwizard_func",
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
function ibm_i_customsql_configwizard_func($mode = "", $inargs = null, &$outargs, &$result)
{
    $wizard_name = "ibm-i-custom-sql";

    // Initialize return code and output
    $result = 0;
    $output = "";

    // Initialize output args - pass back the same data we got
    $outargs[CONFIGWIZARD_PASSBACK_DATA] = $inargs;

    switch ($mode) {
        case CONFIGWIZARD_MODE_GETSTAGE1HTML:

            //$cust_sql = grab_array_var($inargs, "cust_sql");

            $output = '';

            $output .= '
            <h5 class="ul">'._('Instruction').'</h5>
            <p><font size=3>This wizard is used to manage IBM i Custom SQL commands.</font></p>
            <p>You can customize your own commands by leveraging SQL statements. Below are 5 items that you must configure to create your own commands.</p>
            <ul>
                <li>Function Id</li><p>The unique indentifier of your command. Please do not use blank or other special characters for this id.</p>
                <li>Description</li><p>The description for your command.</p>
                <li>Type</li>
                <p>single value: If you choose this type, you must make sure that your SQL statement only return a single value (one row of one column).</p>
                <p>multiple value: multiple value: If you choose this type, your SQL statement could return values from one or two columns.<br>
                If the SQL returns one colunm, the data type of the column returned should be INTEGER or DECIMAL.<br>
                If the SQL returns two colunms, the data type of the first column should be VARCHAR, and the data type of the second column should be INTEGER or DECIMAL.</p>
                <p>list: You could specify as much returning columns/rows as you want. All the returning data will be displayed.</p>
                <li>Thresholds</li><p>The thresholds of your expecting value.</p>
                <li>SQL Command</li><p>The SQL command used to get the data from IBM i server.</p>
            </ul>
            ';
            
                break;

        case CONFIGWIZARD_MODE_VALIDATESTAGE1DATA:

            // Get variables that were passed to us
            
            break;

        case CONFIGWIZARD_MODE_GETSTAGE2HTML:

            $xml = new XMLReader ();
            $xml->open('/usr/local/nagios/etc/objects/CustomSQL.xml');
            $count = -1;
            $name = '';
            $cust_sql = array();
            $xmlTag = array(
                'pre-cmd',
                'post-cmd',
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
                    } else if($node == 'pre-cmd') {
                        $cust_sql[$count]["pre-cmd-type"] = $xml->getAttribute("type");
                        $cust_sql[$count][$node] = $xml->readString();
                    } else if($node == 'post-cmd') {
                        $cust_sql[$count]["post-cmd-type"] = $xml->getAttribute("type");
                        $cust_sql[$count][$node] = $xml->readString();
                    } else if(in_array($node, $xmlTag)) {
                        $name = $node;
                    }
                } else if($xml->nodeType == XMLREADER::TEXT || $xml->nodeType == XMLREADER::CDATA) {
                    if(in_array($name, $xmlTag)) {
                        $cust_sql[$count][$name] = $xml->value;
                    }
                }
            }

            $output .= '
            <h5 class="ul">' . _('Customized Commands Details') . '</h5>
            <table class="adddeleterow table table-condensed table-no-border table-auto-width" style="margin: 0;">
                <tr>
                    <th>' . _('Function Id') . '</th>
                    <th>' . _('Description') . '</th>
                    <th>' . _('Type') . '</th>
                    <th>' . _('Thresholds') . '</th>
                    <th>' . _('SQL Command') . '</th>
                </tr>';
                for ($x = 0; $x < count($cust_sql); $x++) {
                    $output .= '
                    <tr>
                        <td><input type="text" size="30" name="cust_sql[' . $x . '][id]" id="function_id" value="' . encode_form_val($cust_sql[$x]["id"]) . '" class="form-control"></td>
                        <td><input type="text" size="30" name="cust_sql[' . $x . '][common-name]" id="common-name" value="' . encode_form_val($cust_sql[$x]["common-name"]) . '" class="form-control"></td>
                        <td>
                            <select name="cust_sql[' . $x . '][type]" id="type" value="' . encode_form_val($cust_sql[$x]["type"]) . '" class="form-control">
                                <option value="single-value"' . is_selected($cust_sql[$x]["type"], "single-value") . ' >single value</option>
                                <option value="multi-value"' . is_selected($cust_sql[$x]["type"], "multi-value") . ' >multiple value</option>
                                <option value="list"' . is_selected($cust_sql[$x]["type"], "list") . ' >list</option>
                            </select>
                        </td>
                        <td><label><img src="'.theme_image('error.png').'" class="tt-bind" title="'._('Warning Threshold').'"></label>
                            <input type="text" size="2" name="cust_sql['.$x.'][warning]" value="' . encode_form_val($cust_sql[$x]["warning"]) . '" class="form-control condensed">&nbsp;
                            <label><img src="'.theme_image('critical_small.png').'" class="tt-bind" title="'._('Critical Threshold').'"></label>
                            <input type="text" size="2" name="cust_sql['.$x.'][critical]" value="' . encode_form_val($cust_sql[$x]["critical"]) . '" class="form-control condensed">
                        </td>
                        <td><input type="text" size="100" name="cust_sql[' . $x . '][sql-command]" id="sql-command" value="' . encode_form_val($cust_sql[$x]["sql-command"]) . '" class="form-control"></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td></td>
                        <td><p>pre-command</p></td>
                        <td>
                            <select name="cust_sql[' . $x . '][pre-cmd-type]" id="pre-cmd-type" value="' . encode_form_val($cust_sql[$x]["pre-cmd-type"]) . '" class="form-control">
                                <option value="CL"' . is_selected($cust_sql[$x]["pre-cmd-type"], "CL") . ' >CL Command</option>
                                <option value="SQL"' . is_selected($cust_sql[$x]["pre-cmd-type"], "SQL") . ' >SQL Service</option>
                            </select>
                        </td>
                        <td><input type="text" size="100" name="cust_sql[' . $x . '][pre-cmd]" id="pre-cmd" value="' . encode_form_val($cust_sql[$x]["pre-cmd"]) . '" class="form-control"></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td></td>
                        <td><p>post-command</p></td>
                        <td>
                            <select name="cust_sql[' . $x . '][post-cmd-type]" id="post-cmd-type" value="' . encode_form_val($cust_sql[$x]["post-cmd-type"]) . '" class="form-control">
                                <option value="CL"' . is_selected($cust_sql[$x]["post-cmd-type"], "CL") . ' >CL Command</option>
                                <option value="SQL"' . is_selected($cust_sql[$x]["post-cmd-type"], "SQL") . ' >SQL Service</option>
                            </select>
                        </td>
                        <td><input type="text" size="100" name="cust_sql[' . $x . '][post-cmd]" id="post-command" value="' . encode_form_val($cust_sql[$x]["post-cmd"]) . '" class="form-control"></td>
                    </tr>';
                }

                $output .= '</table>';
            break;

        case CONFIGWIZARD_MODE_VALIDATESTAGE2DATA:

            // Get variables that were passed to us
            $cust_sql = "";
            $cust_sql_serial = grab_array_var($inargs, "cust_sql_serial");
            if ($cust_sql_serial != "") {
                $cust_sql = unserialize(base64_decode($cust_sql_serial));
            } else {
                $cust_sql = grab_array_var($inargs, "cust_sql");
            }

            // Check for errors
            $errors = 0;
            $errmsg = array();

            foreach($cust_sql as $service) {
                if (have_value($service["id"]) == false) {
                    $errmsg[$errors++] = _("Custom plugin field function id not specified.");
                }
                if (have_value($service["common-name"]) == false) {
                    $errmsg[$errors++] = _("Custom plugin field description not specified.");
                }
                if (have_value($service["type"]) == false) {
                    $errmsg[$errors++] = _("Custom plugin field type not specified.");
                }
                if (have_value($service["warning"]) == false) {
                    $errmsg[$errors++] = _("Custom plugin field warning not specified.");
                }
                if (have_value($service["critical"]) == false) {
                    $errmsg[$errors++] = _("Custom plugin field critical not specified.");
                }
                if (have_value($service["sql-command"]) == false) {
                    $errmsg[$errors++] = _("Custom plugin field SQL Command not specified.");
                }
            }

            $xml = new XMLWriter();
            $xml->openUri("/usr/local/nagios/etc/objects/CustomSQL.xml");

            $xml->setIndentString("\t");
            $xml->setIndent(true);

            $xml->startDocument('1.0', 'utf-8');
            $xml->startElement("nagios");
            for ($x = 0; $x < count($cust_sql); $x++) {
                $xml->startElement("func");
                    $xml->writeAttribute("id", $cust_sql[$x]["id"]);
                        $xml->startElement("pre-cmd");
                        $xml->writeAttribute("type", $cust_sql[$x]["pre-cmd-type"]);
                        $xml->text($cust_sql[$x]["pre-cmd"]);
                        $xml->endElement();

                        $xml->startElement("post-cmd");
                        $xml->writeAttribute("type", $cust_sql[$x]["post-cmd-type"]);
                        $xml->text($cust_sql[$x]["post-cmd"]);
                        $xml->endElement();

                        $xml->startElement("common-name");
                        $xml->text($cust_sql[$x]["common-name"]);
                        $xml->endElement();
                         
                        $xml->startElement("type");
                        $xml->text($cust_sql[$x]["type"]);
                        $xml->endElement();

                        $xml->startElement("sql-command");
                        $xml->writeCdata($cust_sql[$x]["sql-command"]);
                        $xml->endElement();

                        $xml->startElement("warning");
                        $xml->text($cust_sql[$x]["warning"]);
                        $xml->endElement();

                        $xml->startElement("critical");
                        $xml->text($cust_sql[$x]["critical"]);
                        $xml->endElement();
                $xml->endElement();
            }
            $xml->endElement();
            $xml->endDocument();

            if ($errors > 0) {
                $outargs[CONFIGWIZARD_ERROR_MESSAGES] = $errmsg;
                $result = 1;
            }
            break;

        case CONFIGWIZARD_MODE_GETSTAGE3HTML:

            // Get variables that were passed to us
            $cust_sql = "";
            $cust_sql_serial = grab_array_var($inargs, "cust_sql_serial");
            if ($cust_sql_serial != "") {
                $cust_sql = unserialize(base64_decode($cust_sql_serial));
            } else {
                $cust_sql = grab_array_var($inargs, "cust_sql");
            }

            $output = '
            <input type="hidden" name="cust_sql_serial" value="' . base64_encode(serialize($cust_sql)) . '">';
            break;

        case CONFIGWIZARD_MODE_VALIDATESTAGE3DATA:

            // Get variables that were passed to us
            
            break;

            /////////////OVERRIDE FEATURES FOR CHECK SETTINGS/////////////////
            case CONFIGWIZARD_MODE_GETSTAGE3OPTS:

                //****use this to hardcode the wizard config options ****
                    //the below options are currently the only supported options to override 
                $outargs[CONFIGWIZARD_OVERRIDE_OPTIONS]=array(
                    "max_check_attempts" => 15,
                    "check_interval"        => 15,
                    "retry_interval"        => 15,
                    );
                    
                //hide all form elements for this stage 
                $result=CONFIGWIZARD_HIDE_OPTIONS;
                
                //html output message
                $output.="<p><font size=3><b>Note: </b>IBM i command configuration has been finished. Please click the <b>Finish</b> button.<br /> 
                              The rest pages of the wizard are no use for IBM i command configuration </font>";
            break;
            
            
            /////////////OVERRIDE FEATURES FOR NOTIFICATION SETTINGS/////////////////                       
            case CONFIGWIZARD_MODE_GETSTAGE4OPTS:

                    //****use this to hardcode the wizard config options ****
                        //the below options are currently the only supported options to override 
                    $outargs[CONFIGWIZARD_OVERRIDE_OPTIONS]=array(
                       "notification_delay"        => 15,
                       "notification_interval"        => 15,                        
                    );
                    
                    
                    //hide different notification form elements
                    $outargs[CONFIGWIZARD_HIDDEN_OPTIONS] = array(
                        //the below options are currently the only supported options to hide
                        CONFIGWIZARD_HIDE_NOTIFICATION_OPTIONS,
                        CONFIGWIZARD_HIDE_NOTIFICATION_DELAY,
                        CONFIGWIZARD_HIDE_NOTIFICATION_INTERVAL,
                        CONFIGWIZARD_HIDE_NOTIFICATION_TARGETS, 
                        //NOTE: if contacts options are hidden, the user running the wizard will be set as the contact
                    );

                    $result=CONFIGWIZARD_HIDE_OPTIONS;
                    $output.="<p><font size=3><b>Note: </b>IBM i command configuration has been finished. Please click the <b>Finish</b> button.<br /> 
                              The rest pages of the wizard are no use for IBM i command configuration </font>";
            break;          
            //end override stage4 options

        case CONFIGWIZARD_MODE_GETFINALSTAGEHTML:

            break;

        case CONFIGWIZARD_MODE_GETOBJECTS:

            break;

        default:
            break;
    }

    return $output;
}
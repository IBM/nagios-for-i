#!/usr/bin/env python
# ======================================================================================================================
# Nagios weather and civil alert check
#
# Copyright:        2010, Tony Yarusso
# Author:           Tony Yarusso <tonyyarusso@gmail.com>
# License:          BSD <http://www.opensource.org/licenses/bsd-license.php>
# Homepage:         http://tonyyarusso.com/
# Description:      Checks for active alerts issued by the National Weather Service and other civil authorities.
#                     This works by checking an ATOM feed published by NOAA and parsing detailed information out of
#                     Common Alerting Protocol (CAP) format alerts, and comparing the severity, urgency, and certainty
#                     to the warning and critical thresholds for such.
#
# Revision history is kept in Bazaar at https://code.launchpad.net/~tonyyarusso/+junk/check_cap
#
# Usage: ./check_cap -s State -l Geocodes
#        e.g. ./check_cap -s MN -l 027123
#        e.g. ./check_cap -s MN -l 027123,027053
#
# ----------------------------------------------------------------------------------------------------------------------
#
# Full license text:
#
# Copyright (c) 2010, Tony Yarusso
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
# following conditions are met:
#
#    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
#      disclaimer.
#    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
#      following disclaimer in the documentation and/or other materials provided with the distribution.
#    * Neither the name of Tony Yarusso nor the names of its contributors may be used to endorse or promote products derived
#      from this software without specific prior written permission.

# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
# INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
# SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
# WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#
# ======================================================================================================================

import sys, urllib2, httplib, datetime, getopt
from xml.dom import minidom

level = {
	"critical": {
		"status": [ "Actual" ],
		"msgType": [ "Alert", "Update" ],
		"category": [ "Geo", "Met", "Safety", "Security", "Rescue", "Fire", "Health", "Env", "Transport", "Infra", "CBRNE", "Other" ],
		"urgency": [ "Immediate", "Expected" ],
		"severity": [ "Extreme", "Severe" ],
		"certainty": [ "Observed", "Likely" ]
		},
	"warning": {
		"status": [ "Actual" ],
		"msgType": [ "Alert", "Update" ],
		"category": [ "Geo", "Met", "Safety", "Security", "Rescue", "Fire", "Health", "Env", "Transport", "Infra", "CBRNE", "Other" ],
		"urgency": [ "Immediate", "Expected", "Future", "Unknown" ],
		"severity": [ "Extreme", "Severe", "Moderate", "Minor", "Unknown" ],
		"certainty": [ "Observed", "Likely", "Possible", "Unlikely", "Unknown" ]
		},
	"valid": {
		"status": [ "Actual", "Exercise", "System", "Test", "Draft" ],
		"msgType": [ "Alert", "Update", "Cancel", "Ack", "Error" ],
		"category": [ "Geo", "Met", "Safety", "Security", "Rescue", "Fire", "Health", "Env", "Transport", "Infra", "CBRNE", "Other" ],
		"urgency": [ "Past", "Unknown", "Future", "Expected", "Immediate" ],
		"severity": [ "Unknown", "Minor", "Moderate", "Severe", "Extreme" ],
		"certainty": [ "Unknown", "Unlikely", "Possible", "Likely", "Observed" ]
		}
	}

us_states = {
	"AL": [ "Alabama", "01" ],
	"AK": [ "Alaska", "02" ],
	"AZ": [ "Arizona", "04" ],
	"AR": [ "Arkansas", "05" ],
	"CA": [ "California", "06" ],
	"CO": [ "Colorada", "08" ],
	"CT": [ "Connecticut", "09" ],
	"DE": [ "Delaware", "10" ],
	"DC": [ "District of Columbia", "11" ],
	"FL": [ "Florida", "12" ],
	"GA": [ "Georgia", "13" ],
	"HI": [ "Hawaii", "15" ],
	"ID": [ "Idaho", "16" ],
	"IL": [ "Illinois", "17" ],
	"IN": [ "Indiana", "18" ],
	"IA": [ "Iowa", "19" ],
	"KS": [ "Kansas", "20" ],
	"KY": [ "Kentucky", "21" ],
	"LA": [ "Louisiana", "22" ],
	"ME": [ "Maine", "23" ],
	"MD": [ "Maryland", "24" ],
	"MA": [ "Massachusetts", "25" ],
	"MI": [ "Michigan", "26" ],
	"MN": [ "Minnesota", "27" ],
	"MS": [ "Mississippi", "28"],
	"MO": [ "Missouri", "29" ],
	"MT": [ "Montana", "30" ],
	"NE": [ "Nebraska", "31" ],
	"NV": [ "Nevada", "32" ],
	"NH": [ "New Hampshire", "33" ],
	"NJ": [ "New Jersey", "34" ],
	"NM": [ "New Mexico", "35" ],
	"NY": [ "New York", "36" ],
	"NC": [ "North Carolina", "37" ],
	"ND": [ "North Dakota", "38" ],
	"OH": [ "Ohio", "39" ],
	"OK": [ "Oklahoma", "40" ],
	"OR": [ "Oregon", "41" ],
	"PA": [ "Pennsylvania", "42" ],
	"RI": [ "Rhode Island", "44" ],
	"SC": [ "South Carolina", "45" ],
	"SD": [ "South Dakota", "46" ],
	"TN": [ "Tennessee", "47" ],
	"TX": [ "Texas", "48" ],
	"UT": [ "Utah", "49" ],
	"VT": [ "Vermont", "50" ],
	"VA": [ "Virginia", "51" ],
	"WA": [ "Washington", "53" ],
	"WV": [ "West Virginia", "54" ],
	"WI": [ "Wisconsin", "55" ],
	"WY": [ "Wyoming", "56" ],
	"AS": [ "American Samoa", "60" ],
	"GU": [ "Guam", "66" ],
	"MP": [ "Northern Mariana Islands", "69" ],
	"PW": [ "Palau", "70" ],
	"PR": [ "Puerto Rico", "72" ],
	"UM": [ "U.S. Minor Outlying Islands", "74" ],
	"VI": [ "Virgin Islands of the United States", "78" ],
	"FM": [ "Federated States of Micronesia", "64" ],
	"MH": [ "Marshall Islands", "68" ]
	}
us_state_numbers = {}
for abbr in us_states.keys():
	us_state_numbers[us_states[abbr][1]] = abbr

def usage():
	print "Name:         check_cap"
	print "Description:  Nagios weather and civil alert check"
	print "Author:       Tony Yarusso <tonyyarusso@gmail.com>"
	print "Copyright:    2010, Tony Yarusso"
	print "License:      BSD <http://www.opensource.org/licenses/bsd-license.php>"
	print ""
	print "Usage:  check_cap [-s <ST>] -l <codes>"
	print "e.g. check_cap -s MN -l 027123"
	print "e.g. check_cap -s MN -l 027123,027053"
	print ""
	print "Options:"
	print " -h, --help"
	print "    Print details help screen"
	print " -s, --state, --province"
	print "    Postal abbreviation for the state or province"
	print "    This is optional, but *strongly* recommended for performance"
	print "    reasons.  Default is to check all of the US."
	print " -l, --location"
	print "    FIPS6 or UGC code(s) for the county/ section/zone.  Multiple"
	print "    codes may be provided, separated by commas(with no spaces)."
	print " -w, --warning, --warning-severity"
	print "    Minimum severity to trigger a warning state [Default=Unknown]"
	print "    (One of Unknown, Minor, Moderate, Severe, or Extreme)"
	print " -c, --critical, --critical-severity"
	print "    Minimum severity to trigger a critical state [Default=Severe]"
	print " --warning-urgency"
	print "    Minimum urgency to trigger a warning state [Default=Unknown]"
	print "    (One of Past, Unknown, Future, Expected, or Immediate)"
	print " --critical-urgency"
	print "    Minimum urgency to trigger a critical state [Default=Expected]"
	print " --warning-certainty"
	print "    Minimum certainty to trigger a warning state [Default=Unknown]"
	print "    (One of Unknown, Unlikely, Possible, Likely, Observed)"
	print " --critical-certainty"
	print "    Minimum certainty to trigger a critical state [Default=Likely]"
	print ""
	print "You can look up FIPS6/SAME codes on"
	print "http://www.nws.noaa.gov/nwr/indexnw.htm, UGC county codes on"
	print "http://www.itl.nist.gov/fipspubs/co-codes/states.htm, and UGC zones"
	print "on http://www.nws.noaa.gov/geodata/catalog/wsom/html/pubzone.htm."
	print "(UGC county codes are the postal appreviation, a \"C\", and then the"
	print "last three digits of the FIPS6 codes.)"
	sys.exit(0)

def main(argv):
	try:
		opts, args = getopt.getopt(argv, "hi:s:l:w:c:", ["help", "country=", "state=", "location=", "warning=", "critical=", "warning-urgency=", "critical-urgency=", "warning-severity=", "critical-severity=", "warning-certainty=", "critical-certainty=", "province="])
	except getopt.GetoptError:
		usage()
		sys.exit(127)
	if len(opts) == 0:
		usage()
	state = "US"
	for opt, arg in opts:
		if opt in ("-h", "--help"):
			usage()
		elif opt in ("-l", "--location"):
			location = arg.split(',')
		elif opt in ("-s", "--state", "--province"):
			state = arg
		elif opt in ("-w", "--warning", "--warning-severity"):
			level['warning']['severity'] = level['valid']['severity'][level['valid']['severity'].index(arg):]
		elif opt in ("-c", "--critical", "--critical-severity"):
			level['critical']['severity'] = level['valid']['severity'][level['valid']['severity'].index(arg):]
		elif opt in ("--warning-urgency"):
			level['warning']['urgency'] = level['valid']['urgency'][level['valid']['urgency'].index(arg):]
		elif opt in ("--critical-urgency"):
			level['critical']['urgency'] = level['valid']['urgency'][level['valid']['urgency'].index(arg):]
		elif opt in ("--warning-certainty"):
			level['warning']['certainty'] = level['valid']['certainty'][level['valid']['certainty'].index(arg):]
		elif opt in ("--critical-certainty"):
			level['critical']['certainty'] = level['valid']['certainty'][level['valid']['certainty'].index(arg):]
		else:
			usage()

	#print ""
	request = urllib2.Request('http://www.weather.gov/alerts-beta/'+state.lower()+'.php?x=0')
	request.add_header('User-Agent', 'check_cap/0.1 by Tony Yarusso')
	request.add_header('If-Modified-Since', datetime.datetime.now() - datetime.timedelta(minutes=5))
	opener = urllib2.build_opener()
	feed = opener.open(request)
	xmldoc = minidom.parse(feed)

	#print feed.headers

	affected = False
	active = {}
	highest_severity = highest_certainty = highest_urgency = 0
	for i in range(len(xmldoc.getElementsByTagName('entry'))):
		detailURL = xmldoc.getElementsByTagName('entry')[i].getElementsByTagName('link')[0].attributes["href"].value
		request = urllib2.Request(detailURL)
		opener = urllib2.build_opener()
		detailfile = opener.open(request)
		detailxml = minidom.parse(detailfile)
		
		geocode_tags = detailxml.getElementsByTagName('geocode')
		geocodes = []
		if len(geocode_tags) != 0:
			for code in location:
				for tag in range(len(geocode_tags)):
					if geocode_tags[tag].getElementsByTagName('value')[0].firstChild is not None:
						geocodes.extend(geocode_tags[tag].getElementsByTagName('value')[0].firstChild.data.split())
						if code in geocodes:
							status = xmldoc.getElementsByTagName('entry')[i].getElementsByTagName('cap:status')[0].firstChild.data
							msgType = xmldoc.getElementsByTagName('entry')[i].getElementsByTagName('cap:msgType')[0].firstChild.data
							category = xmldoc.getElementsByTagName('entry')[i].getElementsByTagName('cap:category')[0].firstChild.data
							urgency = xmldoc.getElementsByTagName('entry')[i].getElementsByTagName('cap:urgency')[0].firstChild.data
							severity = xmldoc.getElementsByTagName('entry')[i].getElementsByTagName('cap:severity')[0].firstChild.data
							certainty = xmldoc.getElementsByTagName('entry')[i].getElementsByTagName('cap:certainty')[0].firstChild.data
							if certainty == "Very Likely": # For CAP 1.0 compatibility
								certainty = "Likely"
							if level["valid"]["severity"].index(severity) > highest_severity:
								highest_severity = level["valid"]["severity"].index(severity)
							if level["valid"]["urgency"].index(urgency) - 1 > highest_urgency:
								highest_urgency = level["valid"]["urgency"].index(urgency) - 1
							if level["valid"]["certainty"].index(certainty) > highest_certainty:
								highest_certainty = level["valid"]["certainty"].index(certainty)
							
							if status in level["critical"]["status"] and \
								msgType in level["critical"]["msgType"] and \
								urgency in level["critical"]["urgency"] and \
								severity in level["critical"]["severity"] and \
								certainty in level["critical"]["certainty"]:
								active[detailxml.getElementsByTagName('event')[0].firstChild.data] = 2
							elif status in level["warning"]["status"] and \
								msgType in level["warning"]["msgType"] and \
								urgency in level["warning"]["urgency"] and \
								severity in level["warning"]["severity"] and \
								certainty in level["warning"]["certainty"]:
								active[detailxml.getElementsByTagName('event')[0].firstChild.data] = 1
							else:
								active[detailxml.getElementsByTagName('event')[0].firstChild.data] = 0
				
							affected = True

	if not affected:
		output = "Weather OK: No watches or warnings currently apply to your area."
		status = 0
	else:
		if 2 in active.values():
			output = "Weather Critical: " + ", ".join(active.keys())
			status = 2
		elif 1 in active.values():
			output = "Weather Warning: " + ", ".join(active.keys())
			status = 1
		# + ", see " + detailURL
		else:
			output = "Weather OK: " + ", ".join(active.keys())
			status = 0
	
	output += "|"
	output += "'Severity'=" + str(highest_severity) + ";" + str(4 - len(level["warning"]["severity"]) + 1) + ";" + str(4 - len(level["critical"]["severity"]) + 1) + ";0;4"
	output += " "
	output += "'Urgency'=" + str(highest_urgency) + ";" + str(3 - len(level["warning"]["urgency"]) + 1) + ";" + str(3 - len(level["critical"]["urgency"]) + 1) + ";-1;3"
	output += " "
	output += "'Certainty'=" + str(highest_certainty) + ";" + str(4 - len(level["warning"]["certainty"]) + 1) + ";" + str(4 - len(level["critical"]["urgency"]) + 1)+ ";0;4"
	output += " "
	output += "'Active Alerts'=" + str(len(active)) + ";;;0;"
	print output
	#print ""
	return status

if __name__ == "__main__":
	sys.exit(main(sys.argv[1:]))

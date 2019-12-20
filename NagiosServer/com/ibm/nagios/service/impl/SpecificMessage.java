package com.ibm.nagios.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.data.ProgramCallDocument;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.CommonUtil;
import com.ibm.nagios.util.JDBCConnection;
import com.ibm.nagios.util.Constants;

public class SpecificMessage implements Action {
	public SpecificMessage(){
	}

	public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
		int version = 0;
		int release = 0;
		try {
			version = as400.getVersion();
			release = as400.getRelease();
		} catch (Exception eVR) {
			response.append(Constants.retrieveDataException + " - " + eVR.getMessage());
			return Constants.UNKNOWN;
		}
		Integer returnValue = Constants.UNKNOWN;
		
		Date date = null;
	    Time time = null;
		String msgText = null;
		int severity = 0;
		String user = null;
		Set<String> msgSet = new HashSet<String>();
		Statement stmt = null;
		ResultSet rs = null;
		String sqlType = null;
		String regEx = null;
		int count = 0;
		String messageID = args.get("-I");
		if(messageID == null) {
			response.append("The argument -I [message ID] is not set");
			return returnValue;
		}
		if(messageID.indexOf("*") != -1) {
			sqlType = "like";
			regEx = messageID;
			messageID = messageID.replace("*", "%");
		}
		else {
			sqlType = "in";
			String[] splitTemp = messageID.split(",");
			int msgNum = splitTemp.length;
			messageID = "";
			for(int i=0; i<msgNum; i++) {
				messageID += "'" + splitTemp[i] + "'";
				if(i != msgNum-1) {
					messageID += ",";
				}
			}
		}
		
		if(version > 7 || (version==7 && release>1)) {
			//system version is priority to v7r1, run the plugin by SQL service
			Connection connection = null;
			try {
				JDBCConnection JDBCConn = new JDBCConnection();
				connection = JDBCConn.getJDBCConnection(as400.getSystemName(), args.get("-U"), args.get("-P"), args.get("-SSL"));
				if(connection == null) {
					response.append(Constants.retrieveDataError + " - " + "Cannot get the JDBC connection");
					return returnValue;
				}
				stmt = connection.createStatement();
				if(sqlType.equalsIgnoreCase("like")) {
					rs = stmt.executeQuery("SELECT MESSAGE_ID, FROM_USER, MESSAGE_TEXT, SEVERITY, MESSAGE_TIMESTAMP FROM QSYS2.MESSAGE_QUEUE_INFO WHERE MESSAGE_ID LIKE '" + messageID.toUpperCase() + "'");
				}
				else if(sqlType.equalsIgnoreCase("in")) {
					rs = stmt.executeQuery("SELECT MESSAGE_ID, FROM_USER, MESSAGE_TEXT, SEVERITY, MESSAGE_TIMESTAMP FROM QSYS2.MESSAGE_QUEUE_INFO WHERE MESSAGE_ID IN (" + messageID.toUpperCase() + ")");
				}
				if(rs == null) {
					response.append(Constants.retrieveDataError + " - " + "Cannot retrieve data from server");
					return returnValue;
				}			
				while(rs.next()) {
					user = rs.getString("FROM_USER");
					severity = rs.getInt("SEVERITY");
					date = rs.getDate("MESSAGE_TIMESTAMP");
			        time = rs.getTime("MESSAGE_TIMESTAMP");
					messageID = rs.getString("MESSAGE_ID");
					msgText = rs.getString("MESSAGE_TEXT");
					msgSet.add(messageID);
					count++;
					returnValue = Constants.WARN;
					response.append("Message ID: " + messageID + " User: " + user + " Severity: " + severity + " Date: " + date + " Time: " + time + " Text: " + msgText + "\n");
				}
				if(count == 0) {
					returnValue = Constants.OK;
					response.append("Status: OK");
				}
				else {
					response.insert(0, "Message counts: " + count + "\n");
				}
			}
			catch(Exception e) {
				response.setLength(0);
				response.append(Constants.retrieveDataException + " - " + e.toString());
				CommonUtil.printStack(e.getStackTrace(), response);
				e.printStackTrace();
			}
			finally {
				date = null;
				time = null;
				try {
					if(rs != null)
						rs.close();
					if(stmt != null)
						stmt.close();
					if(connection != null)
						connection.close();
				} catch (SQLException e) {
					response.append(Constants.retrieveDataException + " - " + e.toString());
					e.printStackTrace();
				}
			}
		}
		else {
			//system version is v7r1 or below, run the plugin by API
			ProgramCallDocument pcml = null;
			boolean rc = false;
			String msgID = null;
			String strTime = null;
			String strDate = null;
			String m_parameter;
			
			try {
				pcml = new ProgramCallDocument(as400, "com.ibm.nagios.util.qgyolmsg");
				int[] fieldIndex  = new int[1];
	 
				// Set selection criteria value
				m_parameter = "qgyolmsg.msgSelInfo.selectionCriteria";
				fieldIndex[0] = 0;
				pcml.setValue(m_parameter, fieldIndex, "*ALL      " );

				// Set message queue information for the list
				m_parameter = "qgyolmsg.userOrQueueInfo.userOrQueueIndicator";
				pcml.setValue(m_parameter, "1");
				m_parameter = "qgyolmsg.userOrQueueInfo.userOrQueueName";
				pcml.setValue(m_parameter, "QSYSOPR   QSYS      ");
	 
				// Set starting message key 
				m_parameter = "qgyolmsg.msgSelInfo.messageKeys";
				fieldIndex[0] = 0;
				pcml.setValue(m_parameter, fieldIndex, 0);
				fieldIndex[0] = 1;
				pcml.setValue(m_parameter, fieldIndex, 65535);
	 
				// Set fields to return
				int[] keyIndex;
				int   keyValue;
				keyIndex = new int[1];        				
	 
				m_parameter = "qgyolmsg.msgSelInfo.fldsToReturn";
				keyIndex[0] = 0;
				keyValue = 301;  // Message
				pcml.setIntValue(m_parameter, keyIndex, keyValue);
				
				keyIndex[0] = 1;
				keyValue = 607;  // User profile
				pcml.setIntValue(m_parameter, keyIndex, keyValue);
				
				keyIndex[0] = 2;
				keyValue = 1001;  // Reply status
				pcml.setIntValue(m_parameter, keyIndex, keyValue);
				
				rc = pcml.callProgram("qgyolmsg");
				if (rc == false) {
					// Print out all of the AS/400 messages
				    AS400Message[] messageList = pcml.getMessageList("qgyolmsg");
				    for (int i=0; i<messageList.length; i++) { 
				    	response.append(Constants.retrieveDataException + " - " + messageList[i].getID() + "  " + messageList[i].getText());
					}
					return Constants.UNKNOWN;
		        }
				
				// Get number messages returned    	
		        m_parameter = "qgyolmsg.listInfo.rcdsReturned";
		        int nbrEntries = pcml.getIntValue(m_parameter);
		        int index[] = new int[1];
		        fieldIndex = new int[2];
		        for(int i=0; i<nbrEntries; i++) {
		        	index[0] = i;
		        	 
		        	// Retrieve message ID
	            	msgID = pcml.getStringValue("qgyolmsg.lstm0100.msgEntry.msgID", index).trim();
	            	 
	            	// Retrieve message type
		        	//type = pcml.getStringValue("qgyolmsg.lstm0100.msgEntry.msgType", index).trim();
		        	 
		        	// Retrieve sent time
		        	strTime = pcml.getStringValue("qgyolmsg.lstm0100.msgEntry.timeSent", index);
		        	 
		        	// Retrieve sent date
		        	strDate = pcml.getStringValue("qgyolmsg.lstm0100.msgEntry.dateSent", index).substring(1);
		        	 
		        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
		        	Date dateTime = dateFormat.parse(strDate+strTime);
		        	 
		        	// Retrieve filed number
		            int nbrKeyFields = pcml.getIntValue("qgyolmsg.lstm0100.msgEntry.nbrOfFields", index);
		             
		            for(int j=0; j<nbrKeyFields; j++) {
		            	fieldIndex[0] = i;
		            	fieldIndex[1] = j;
			        	 
		            	// Retrieve key field data
		                byte[] keyData = (byte[])pcml.getValue("qgyolmsg.lstm0100.msgEntry.fieldInfo.keyData", fieldIndex);

		                // Retrieve field key
		                int dataKey = pcml.getIntValue("qgyolmsg.lstm0100.msgEntry.fieldInfo.keyField", fieldIndex);
		                 
		                // Retrieve data length
		            	int dataLength = pcml.getIntValue("qgyolmsg.lstm0100.msgEntry.fieldInfo.lengthOfData", fieldIndex);
		            	 
		            	switch (dataKey) {               
		                   case 301:
		                	   // Set the message: Char(*)
		                	   if (dataLength > 0) {
		                		   AS400Text text = new AS400Text(dataLength, as400.getCcsid(), as400);
		                		   msgText = (String)text.toObject(keyData, 0);
		                		   text = null;
		                	   }
		                	   break;
		                   case 607:
		                    	// Set the user profile: Char(*)
		                    	if (dataLength > 0) {
		                    		AS400Text text = new AS400Text(dataLength, as400.getCcsid(), as400);
		                    		user = (String)text.toObject(keyData, 0);
		                    		text = null;
		                    }
		                    break;
		            	 }
		            }
		            if(sqlType.equalsIgnoreCase("in") && messageID.contains(msgID)) {
		            	response.append("Message ID: " + msgID + " User: " + user + " Severity: " + severity + " Time: " + dateTime + " Text: " + msgText + "\n");
		            	msgSet.add(msgID);
		            	count++;
						returnValue = Constants.WARN;
		            }
		            else if(sqlType.equalsIgnoreCase("like")) {
		            	if(Pattern.compile(regEx).matcher(msgID).find()) {
		            		response.append("Message ID: " + msgID + " User: " + user + " Severity: " + severity + " Time: " + dateTime + " Text: " + msgText + "\n");
		            		msgSet.add(msgID);
		            		count++;
							returnValue = Constants.WARN;
		            	}
		            }
		        }
		        if(count == 0) {
					returnValue = Constants.OK;
					response.append("Status: OK");
				}
				else {
					response.insert(0, "Message counts: " + count + "\n");
				}
		         
		        m_parameter = "qgyolmsg.listInfo.rqsHandle";
		        Object handle = pcml.getValue(m_parameter);

		        m_parameter = "qgyclst.closeHand";
		        pcml.setValue(m_parameter, handle);
		          					
		        m_parameter = "qgyclst";
		        pcml.callProgram("qgyclst");
			}
			catch(Exception e) {
				response.setLength(0);
				response.append(Constants.retrieveDataException + " - " + e.toString());
			}
		}
		return returnValue;
	}
}

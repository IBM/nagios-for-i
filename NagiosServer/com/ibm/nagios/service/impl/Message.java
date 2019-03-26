package com.ibm.nagios.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.data.ProgramCallDocument;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.CommonUtil;
import com.ibm.nagios.util.JDBCConnection;
import com.ibm.nagios.util.StatusConstants;

public class Message implements Action {
	private static String BLANK10 = "          ";
	private String[] MESSAGE_TYPE = {
			null,
			"COMPLETION",		//01
			"DIAGNOSTIC",		//02
			null,
			"INFORMATIONAL",	//04
			"INQUIRY",			//05
			"SENDER",			//06
			null,
			"REQUEST",			//08
			null,
			"REQUEST",			//10
			null, null, null,
			"NOTIFY",			//14
			"ESCAPE",			//15
			"NOTIFY",			//16
			"ESCAPE",			//17
			null, null, null,
			"REPLY", "REPLY", "REPLY", "REPLY", "REPLY", "REPLY"	//21, 22, 23, 24, 25, 26
	};
	public Message(){
	}

	public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
		int version = 0;
		int release = 0;
		try {
			version = as400.getVersion();
			release = as400.getRelease();
		} catch (Exception eVR) {
			response.append(StatusConstants.retrieveDataException + "| " + eVR.getMessage());
			return StatusConstants.UNKNOWN;
		}
		int messageNum = 0;
		int severity = 0;
		String type = null;
		String msgText = null;
		String msgID = null;
		String user = null;
		Date date = null;
	    Time time = null;
		Statement stmt = null;
		ResultSet rs = null;
		String msgQueLib = args.get("-LIB");
		msgQueLib = msgQueLib==null ? "QSYS" : msgQueLib.trim().toUpperCase();
		String msgQueName = args.get("-NAME");
		msgQueName = msgQueName==null ? "QSYSOPR" : msgQueName.trim().toUpperCase();
		String msgType = args.get("-TY");
		msgType = msgType==null ? "INQUIRY, ESCAPE, REPLY" : msgType.trim().toUpperCase();
		String types[] = msgType.split(",");
		String requireType = "";
		for(int i=0; i<types.length; i++) {
			requireType = requireType + "'" + types[i].trim() + "'";
			if(i!=types.length-1)
				requireType += ",";
		}
		Integer returnValue = StatusConstants.UNKNOWN;
		
		String warningCap = args.get("-W");
		String criticalCap = args.get("-C");
		int intWarningCap = (warningCap == null) ? 100 : Integer.parseInt(warningCap);
		int intCriticalCap = (criticalCap == null) ? 100 : Integer.parseInt(criticalCap);
		
		if(version > 7 || (version==7 && release>1)) {
			//system version is priority to v7r1, run the plugin by SQL service
			Connection connection = null;
			try {
				JDBCConnection JDBCConn = new JDBCConnection();
				connection = JDBCConn.getJDBCConnection(as400.getSystemName(), args.get("-U"), args.get("-P"), args.get("-SSL"));
				if(connection == null) {
					response.append(StatusConstants.retrieveDataError + "| " + "Cannot get the JDBC connection");
					return returnValue;
				}
				stmt = connection.createStatement();
				rs = stmt.executeQuery("SELECT MESSAGE_ID, FROM_USER, MESSAGE_TYPE, MESSAGE_TEXT, SEVERITY, MESSAGE_TIMESTAMP FROM QSYS2.MESSAGE_QUEUE_INFO WHERE MESSAGE_QUEUE_LIBRARY = '" + msgQueLib.trim() + "' AND MESSAGE_QUEUE_NAME = '" + msgQueName.trim() + "' AND MESSAGE_TYPE IN (" + requireType + ")");
				if(rs == null) {
					response.append(StatusConstants.retrieveDataError + "| " + "Cannot retrieve data from server");
					return returnValue;
				}			
				while(rs.next()) {
					msgID = rs.getString("MESSAGE_ID");
					user = rs.getString("FROM_USER");
					type = rs.getString("MESSAGE_TYPE");
					msgText = rs.getString("MESSAGE_TEXT");
					date = rs.getDate("MESSAGE_TIMESTAMP");
			        time = rs.getTime("MESSAGE_TIMESTAMP");
					severity = rs.getInt("SEVERITY");
					response.append("ID: " + msgID + " Text: " + msgText + "\n");
					response.append("User: " + user + " Type: " + type + " Severity: " + severity + " Date Sent: " + date + " Time Sent: " + time + "\n");
					messageNum++;
				}
				
				returnValue = CommonUtil.getStatus(messageNum, intWarningCap, intCriticalCap, returnValue);
				response.insert(0, "Message Num in type " + requireType + ": " + messageNum + "\n");
			}
			catch(Exception e) {
				response.setLength(0);
				response.append(StatusConstants.retrieveDataException + "| " + e.getMessage());
				CommonUtil.printStack(e.getStackTrace(), response);
			}
			finally {
				date = null;
			    time = null;
				types = null;
				try {
					if(rs != null)
						rs.close();
					if(stmt != null)
						stmt.close();
					if(connection != null)
						connection.close();
				} catch (SQLException e) {
					response.append(StatusConstants.retrieveDataException + "| " + e.getMessage());
				}
			}
		}
		else {
			//system version is v7r1 or below, run the plugin by API
			ProgramCallDocument pcml = null;
			boolean rc = false;
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
				msgQueName += BLANK10.substring(msgQueName.length());
				msgQueLib += BLANK10.substring(msgQueLib.length());
				pcml.setValue(m_parameter, msgQueName + msgQueLib);
	 
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
				    	response.append(StatusConstants.retrieveDataException + "| " + messageList[i].getID() + "  " + messageList[i].getText());
					}
					return StatusConstants.UNKNOWN;
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
		        	type = pcml.getStringValue("qgyolmsg.lstm0100.msgEntry.msgType", index).trim();
		        	 
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
		            type = MESSAGE_TYPE[Integer.parseInt(type)];
		            if(msgType.contains(type)) {
		            	response.append("ID: " + msgID + " Text: " + msgText + "\n");
		            	response.append("User: " + user + " Type: " + type + " Severity: " + severity + " Time Sent: " + dateTime + "\n");
		            	messageNum++;
		            }
		        }
		        
		        returnValue = CommonUtil.getStatus(messageNum, intWarningCap, intCriticalCap, returnValue);
		        response.insert(0, "Message Num in type " + requireType + ": " + messageNum + "\n");

		        m_parameter = "qgyolmsg.listInfo.rqsHandle";
		        Object handle = pcml.getValue(m_parameter);

		        m_parameter = "qgyclst.closeHand";
		        pcml.setValue(m_parameter, handle);
		          					
		        m_parameter = "qgyclst";
		        pcml.callProgram("qgyclst");
			}
			catch(Exception e) {
				response.setLength(0);
				response.append(StatusConstants.retrieveDataException + "| " + e.getMessage());
				e.printStackTrace();
			}
		}
		return returnValue;
	}
}

package com.ibm.nagios.service.impl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.as400.access.AS400;
import com.ibm.nagios.service.Action;
import com.ibm.nagios.util.CommonUtil;
import com.ibm.nagios.util.JDBCConnection;
import com.ibm.nagios.util.Constants;

public class CustomSQL implements Action {
	private final static String CUSTOM_SQL = "/usr/local/nagios/etc/objects/CustomSQL.xml";
	//private final static String CUSTOM_SQL = "C:/Users/IBM_ADMIN/Desktop/Nagios/etc/objects/CustomSQL.xml";
	private static String commonName = null;
	private static String type = null;
	private static String sqlCmd = null;
	public CustomSQL(){
	}

	public int execute(AS400 as400, Map<String, String> args, StringBuffer response) {
		Statement stmt = null;
		ResultSet rs = null;
		int index = 1;	//for multi value's id field
		int count = 0;	//for record result number
		String outputValue = "";
		ArrayList<String[]> columns = new ArrayList<String[]>();
		String func = args.get("-F");
		String warningCap = args.get("-W");
		String criticalCap = args.get("-C");
		int returnValue = Constants.UNKNOWN;
		
		if(func == null) {
			response.append("The argument -F [function name] is not set");
			return returnValue;
		}
		
		Connection connection = null;
		try {
			JDBCConnection JDBCConn = new JDBCConnection();
			connection = JDBCConn.getJDBCConnection(as400.getSystemName(), args.get("-U"), args.get("-P"), args.get("-SSL"));
			if(connection == null) {
				response.append(Constants.retrieveDataError + "| " + "Cannot get the JDBC connection");
				return returnValue;
			}
			loadSQL(func);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sqlCmd);
			if(rs == null) {
				response.append(Constants.retrieveDataError + "| " + "Cannot retrieve data from server");
				return returnValue;
			}
			ResultSetMetaData rsmtadta = rs.getMetaData();
			int colCount = rsmtadta.getColumnCount(); 
			for (int i=1; i<= colCount; i++) {
				 String colName = rsmtadta.getColumnName(i);    
				 String colType = rsmtadta.getColumnTypeName(i);
				 columns.add(new String[]{colName, colType});
			}
			if(type.equalsIgnoreCase("single-value")) {
				if(rs.next()) {
					if(columns.size() == 1) {
						String colName = columns.get(0)[0];
						String colType = columns.get(0)[1];							
						if(colType.equalsIgnoreCase("INTEGER")) {
							int intWarningCap = (warningCap == null) ? 100 : Integer.parseInt(warningCap);
							int intCriticalCap = (criticalCap == null) ? 100 : Integer.parseInt(criticalCap);
							int value = rs.getInt(colName);
							
							returnValue = CommonUtil.getStatus(value, intWarningCap, intCriticalCap, returnValue);
							response.append(commonName + ": " + value + " | '" + commonName + "' = " + value + ";" + intWarningCap + ";" + intCriticalCap);
						}
						else if(colType.equalsIgnoreCase("DECIMAL")) {
							double doubleWarningCap = (warningCap == null) ? 100 : Double.parseDouble(warningCap);
							double doubleCriticalCap = (criticalCap == null) ? 100 : Double.parseDouble(criticalCap);
							double value = rs.getDouble(colName);
							
							returnValue = CommonUtil.getStatus(value, doubleWarningCap, doubleCriticalCap, returnValue);
							response.append(commonName + ": " + value + " | '" + commonName + "' = " + value + ";" + doubleWarningCap + ";" + doubleCriticalCap);
						}
					}
					else {
						response.append("Error: Wrong SQL command format");
					}
				}
			}
			else if(type.equalsIgnoreCase("muti-value") || type.equalsIgnoreCase("multi-value")) {
				String idName = null;
				String colName = null;
				String colType = null;
				
				if(columns.size() == 1) {
					colName = columns.get(0)[0];
					colType = columns.get(0)[1];
				}
				else if(columns.size() == 2) {
					idName = columns.get(0)[0];
					colName = columns.get(1)[0];
					colType = columns.get(1)[1];
				}
				else {
					response.append("Error: Wrong SQL command format");
					return Constants.UNKNOWN;
				}
				while(rs.next()) {
					String id = "";
					if(idName != null) {
						id = rs.getString(idName);
					}
					else {
						id = String.valueOf(index++);
					}
					if(colType.equalsIgnoreCase("INTEGER")) {
						int maxVal = 0;
						int intWarningCap = (warningCap == null) ? 100 : Integer.parseInt(warningCap);
						int intCriticalCap = (criticalCap == null) ? 100 : Integer.parseInt(criticalCap);
						
						int value = rs.getInt(colName);
						
						if(value > maxVal) {
							maxVal = value;
							outputValue = String.valueOf(maxVal);
						}
						returnValue = CommonUtil.getStatus(value, intWarningCap, intCriticalCap, returnValue);
						response.append("'" + id + "' = " + value + ";" + intWarningCap + ";" + intCriticalCap);
					}
					else if(colType.equalsIgnoreCase("DECIMAL")) {
						double maxVal = 0;
						double doubleWarningCap = (warningCap == null) ? 100 : Double.parseDouble(warningCap);
						double doubleCriticalCap = (criticalCap == null) ? 100 : Double.parseDouble(criticalCap);
						double value = rs.getDouble(colName);
						
						if(value > maxVal) {
							maxVal = value;
							outputValue = String.valueOf(maxVal);
						}
						returnValue = CommonUtil.getStatus(value, doubleWarningCap, doubleCriticalCap, returnValue);
						response.append("'" + id + "' = " + value + ";" + doubleWarningCap + ";" + doubleCriticalCap);
					}
				}
				response.insert(0, commonName + ": " + outputValue + " | ");
			}
			else if(type.equalsIgnoreCase("list")) {
				double intWarningCap = (warningCap == null) ? 1 : Double.parseDouble(warningCap);
				double intCriticalCap = (criticalCap == null) ? 1 : Double.parseDouble(criticalCap);
				while(rs.next()) {
					for(int i=0; i<columns.size(); i++) {
						String colName = columns.get(i)[0];
						String value = rs.getString(colName);
						response.append(colName + ": " + value + " ");
					}
					response.append("\n");
					count++;
				}
				
				returnValue = CommonUtil.getStatus(count, intWarningCap, intCriticalCap, returnValue);
				response.insert(0, commonName + " count of record: " + count + "\n");
			}
		}
		catch(Exception e) {
			response.append(Constants.retrieveDataException + "| " + e.getMessage()==null?e.toString():e.getMessage());
			CommonUtil.printStack(e.getStackTrace(), response);
			e.printStackTrace();
		}
		finally {
			try {
				if(rs != null)
					rs.close();
				if(stmt != null)
					stmt.close();
				if(connection != null)
					connection.close();
			} catch (SQLException e) {
				response.append(Constants.retrieveDataException + "| " + e.getMessage()==null ? e.toString() : e.getMessage());
				e.printStackTrace();
			}
		}
		return returnValue;
	}
	
	private void loadSQL(String funcID) throws Exception {
		InputStream is = new FileInputStream(CUSTOM_SQL);
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = db.parse(is);
		Element root = document.getDocumentElement();
		NodeList list = root.getElementsByTagName("func");
		boolean findFlag = false;
		Node node = null;
		Node child = null;
		String function = null;
		
		for(int i=0; i<list.getLength(); i++) {
			node = list.item(i);
			function = node.getAttributes().getNamedItem("id").getNodeValue().toLowerCase();
			if(function.equalsIgnoreCase(funcID)) {
				findFlag = true;
				break;
			}
		}
		if(!findFlag) {
			throw new Exception("cannot find the function " + funcID + " in " + CUSTOM_SQL);
		}
		NodeList childList = node.getChildNodes();
		for(int i=0; i<childList.getLength(); i++) {
			child = childList.item(i);
			if(child.getNodeType()!=Node.ELEMENT_NODE)
				continue;
			if(child.getNodeName().equalsIgnoreCase("common-name")) {
				commonName = child.getTextContent();
			}
			if(child.getNodeName().equalsIgnoreCase("type")) {
				type = child.getTextContent();
			}
			if(child.getNodeName().equalsIgnoreCase("sql-command")) {
				sqlCmd = child.getTextContent();
			}
		}
	}
}
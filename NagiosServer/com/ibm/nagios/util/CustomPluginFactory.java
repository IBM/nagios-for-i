package com.ibm.nagios.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CustomPluginFactory {
	private final static String CUSTOM_SQL = "/usr/local/nagios/etc/objects/CustomSQL.xml";
	private final static String CUSTOM_SQL_SAMPLE = "/usr/local/nagios/etc/objects/CustomSQL.xml.sample";
	
	private static Map<String, CustomBean> custHandler = new ConcurrentHashMap<String, CustomBean>();
	
	public static void load() throws Exception {
		File custSql = new File(CUSTOM_SQL);
		if(!custSql.exists()) {
			File custSample = new File(CUSTOM_SQL_SAMPLE);
			custSample.renameTo(custSql);
		}
		InputStream is = new FileInputStream(CUSTOM_SQL);
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = db.parse(is);
		Element root = document.getDocumentElement();
		NodeList list = root.getElementsByTagName("func");
		Node node = null;
		Node child = null;
		Node cmdNode = null;
		String funcId = null;
		String commonName = null;
		String type = null;
		String sqlCmd = null;
		String warning = null;
		String critical = null;
		ArrayList<CustomCommand> preCmd = new ArrayList<CustomCommand>();
		ArrayList<CustomCommand> postCmd = new ArrayList<CustomCommand>();
		
		for(int i=0; i<list.getLength(); i++) {
			node = list.item(i);
			funcId = node.getAttributes().getNamedItem("id").getNodeValue().toLowerCase();
			
			NodeList childList = node.getChildNodes();
			for(int j=0; j<childList.getLength(); j++) {
				child = childList.item(j);
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
				if(child.getNodeName().equalsIgnoreCase("warning")) {
					warning = child.getTextContent();
				}
				if(child.getNodeName().equalsIgnoreCase("critical")) {
					critical = child.getTextContent();
				}
				if(child.getNodeName().equalsIgnoreCase("pre-cmd")) {
					NodeList cmdList = child.getChildNodes();
					for(int k=0; k<cmdList.getLength(); k++) {
						cmdNode = cmdList.item(k);
						if(cmdNode.getNodeType()!=Node.ELEMENT_NODE)
							continue;
						String command = cmdNode.getTextContent();
						String cmdType = cmdNode.getAttributes().getNamedItem("type").getNodeValue();
						if(command == null) {
							throw new Exception("CustomSQL.xml format error: pre command is not set:" + funcId);
						}
						if(cmdType == null) {
							throw new Exception("CustomSQL.xml format error: precommand type is not set:" + command);
						}
						CustomCommand cmdObj = new CustomCommand(command, cmdType);
						preCmd.add(cmdObj);
					}
				}
				if(child.getNodeName().equalsIgnoreCase("post-cmd")) {
					NodeList cmdList = child.getChildNodes();
					for(int k=0; k<cmdList.getLength(); k++) {
						cmdNode = cmdList.item(k);
						if(cmdNode.getNodeType()!=Node.ELEMENT_NODE)
							continue;
						String command = cmdNode.getTextContent();
						String cmdType = cmdNode.getAttributes().getNamedItem("type").getNodeValue();
						if(command == null) {
							throw new Exception("CustomSQL.xml format error: post command is not set:" + funcId);
						}
						if(cmdType == null) {
							throw new Exception("CustomSQL.xml format error: post command type is not set:" + command);
						}
						CustomCommand cmdObj = new CustomCommand(command, cmdType);
						postCmd.add(cmdObj);
					}
				}
			}
			
			custHandler.put(funcId.toLowerCase(), new CustomBean(commonName, type, sqlCmd, warning, critical, preCmd, postCmd));//store the function id as lower case
		}
	}
	
	public static CustomBean get(String funcId) throws Exception {
		funcId = funcId.toLowerCase();
		CustomBean custBean = custHandler.get(funcId);
		if(custBean == null) {
			load();
			custBean = custHandler.get(funcId);
		}
		return custBean;
	}

}

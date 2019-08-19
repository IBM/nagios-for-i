package com.ibm.nagios.util;

import java.io.FileInputStream;
import java.io.InputStream;
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
	
	private static Map<String, CustomBean> custHandler = new ConcurrentHashMap<String, CustomBean>();
	
	public static void load() throws Exception {
		InputStream is = new FileInputStream(CUSTOM_SQL);
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = db.parse(is);
		Element root = document.getDocumentElement();
		NodeList list = root.getElementsByTagName("func");
		Node node = null;
		Node child = null;
		String funcId = null;
		String commonName = null;
		String type = null;
		String sqlCmd = null;
		
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
			}
			
			custHandler.put(funcId.toLowerCase(), new CustomBean(commonName, type, sqlCmd));//store the function id as lower case
		}
	}
	
	public static CustomBean get(String funcId) {
		return custHandler.get(funcId);
	}
}

package com.ibm.nagios.hmc;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HMCPluginFactory {
	private final static String HMC_REST_API = "/usr/local/nagios/etc/objects/HMCRestAPI.xml";
	
	private static Map<String, HMCBean> hmcHandler = new ConcurrentHashMap<String, HMCBean>();
	
	public static void load() throws Exception {
		InputStream is = new FileInputStream(HMC_REST_API);
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = db.parse(is);
		Element root = document.getDocumentElement();
		NodeList list = root.getElementsByTagName("func");
		Node node = null;
		Node child = null;
		Node key = null;
		String funcId = null;
		String commonName = null;
		List<String> keyList = new ArrayList<String>();
		String restAPI = null;
		
		for(int i=0; i<list.getLength(); i++) {
			node = list.item(i);
			funcId = node.getAttributes().getNamedItem("id").getNodeValue().toLowerCase();
			keyList.clear();
			NodeList childList = node.getChildNodes();
			for(int j=0; j<childList.getLength(); j++) {
				child = childList.item(j);
				if(child.getNodeType()!=Node.ELEMENT_NODE)
					continue;
				if(child.getNodeName().equalsIgnoreCase("common-name")) {
					commonName = child.getTextContent();
				}
				if(child.getNodeName().equalsIgnoreCase("keys")) {
					NodeList keyNodeList = child.getChildNodes();
					for(int k=0; k<keyNodeList.getLength(); k++) {
						key = keyNodeList.item(k);
						if(key.getNodeType()!=Node.ELEMENT_NODE)
							continue;
						if(key.getNodeName().equalsIgnoreCase("key")) {
							keyList.add(key.getTextContent());
						}
					}
				}
				if(child.getNodeName().equalsIgnoreCase("rest-api")) {
					restAPI = child.getTextContent();
				}
			}
			
			hmcHandler.put(funcId.toLowerCase(), new HMCBean(commonName, keyList, restAPI));//store the function id as lower case
		}
	}
	
	public static HMCBean get(String funcId) throws Exception {
		funcId = funcId.toLowerCase();
		HMCBean custBean = hmcHandler.get(funcId);
		if(custBean == null) {
			load();
			custBean = hmcHandler.get(funcId);
		}
		return custBean;
	}
}

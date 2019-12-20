package com.ibm.nagios.hmc.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.nagios.hmc.HMCConstants;
import com.ibm.nagios.hmc.Utilities;

public class XmlParser {
	InputStream input;
	String str;
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	Document doc = null;
	
	/**
	 * constructor
	 * @param input, the xml stream that you want to parse
	 */
	public XmlParser(InputStream input) {
		this.input = input;
	}
	
	public XmlParser(String str) {
		this.str = str;
	}
	
	private void init() throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilder builder = factory.newDocumentBuilder();
		if(input!=null) {
			doc = builder.parse(input);
		}else {
			doc = builder.parse(str);
		}
	}
	
	/**
	 * get the X-API session id
	 * @return
	 * @throws Exception
	 */
	public String getXAPISession() throws Exception {
		if(doc==null) {
			init();
		}
		
		NodeList nodes = doc.getElementsByTagName(HMCConstants.XML_SESSION_ID);
		Node xAPI = nodes.item(0);
		return xAPI.getChildNodes().item(0).getNodeValue();
	}
	
	/**
	 * get the job status
	 * @return
	 * @throws Exception
	 */
	public String getJobStatus() throws Exception{
		if(doc==null) {
			init();
		}
	    
	    NodeList nodes = doc.getElementsByTagName(HMCConstants.XML_JOB_STATUS);
	    Node jobStatus = nodes.item(0);
	    return jobStatus.getChildNodes().item(0).getNodeValue();
	}
	
	/**
	 * Get the jod id
	 * @return
	 * @throws Exception
	 */
	public String getJobID() throws Exception{
		if(doc==null) {
			init();
		}
	    
	    NodeList nodes = doc.getElementsByTagName(HMCConstants.XML_JOB_ID);
	    Node jobID = nodes.item(0);
	    return jobID.getChildNodes().item(0).getNodeValue();
	}
	
	/**
	 * Get the partition uuid
	 * @return
	 * @throws Exception
	 */
	public String getUUID() throws Exception {
		if(doc==null) {
			init();
		}
	    
	    NodeList nodes = doc.getElementsByTagName(HMCConstants.XML_PARTITION_UUID);
	    Node partitionUUID = nodes.item(0);
	    return partitionUUID.getChildNodes().item(0).getNodeValue();
	}
	
	/**
	 * get the UUID of a specific partition
	 * @param partitionName, partition name
	 * @return
	 * @throws Exception
	 */
	public String getUUID(String partitionName) throws Exception {
		if(doc==null) {
			init();
		}
	    
	    NodeList nodes = doc.getElementsByTagName(HMCConstants.XML_PARTITION_NAME);
	    Node entry = null;
	    for(int i=0; i<nodes.getLength(); i++) {
	    	Node node = nodes.item(i);
	    	if(node.getChildNodes().item(0).getNodeValue().equalsIgnoreCase(partitionName)) {
	    		entry = node.getParentNode();
	    		break;
	    	}
	    }
	    
	    if(entry==null) {
	    	throw new Exception("Did not find partition entry for name " + partitionName);
	    }
	    
	    NodeList entryNodes = entry.getChildNodes();
	    Node partitionUUID=null;
	    for(int i=0; i<entryNodes.getLength(); i++) {
	    	Node node = entryNodes.item(i);
	    	if(node.getNodeName().equalsIgnoreCase(HMCConstants.XML_PARTITION_UUID)){
	    		partitionUUID = node;
	    		break;
	    	}
	    }
	    return partitionUUID!=null? partitionUUID.getChildNodes().item(0).getNodeValue() : null;
	}
	
	/**
	 * get the node list of the specified node name 
	 * @param nodeName, the node name
	 * @return, the node object list
	 * @throws Exception
	 */
	public List<Node> getNodes(String nodeName) throws Exception{
		if(doc==null) {
			init();
		}
	    
		List<Node> nodeList = new ArrayList<Node>();
	    NodeList nodes = doc.getElementsByTagName(nodeName);
	    for(int i=0; i<nodes.getLength(); i++) {
	    	Node node = nodes.item(i);
	    	nodeList.add(node);
	    }
	    return nodeList;
	}
	
	/**
	 * get the children node list of the specified parent, with the specified node name 
	 * @param parent, parent Node object
	 * @param nodeName, the node name of the child
	 * @return, the node object list
	 * @throws Exception
	 */
	public List<Node> getNodes(Node parent, String nodeName) throws Exception{
		if(doc==null) {
			init();
		}
	    
		List<Node> nodes = new ArrayList<Node>();
		NodeList children = parent.getChildNodes();
	    for(int i=0; i<children.getLength(); i++) {
	    	Node node = children.item(i);
	    	if(nodeName.equalsIgnoreCase(node.getNodeName())) {
	    		nodes.add(node);
	    	}
	    }
	    return nodes;
	}

	/**
	 * get the node value, only applicable for names that only have one node entry 
	 * @param nodeName, the node name
	 * @return, the node value
	 * @throws Exception
	 */
	public String getNodeValue(String nodeName) throws Exception{
		if(doc==null) {
			init();
		}
	    
	    NodeList nodes = doc.getElementsByTagName(nodeName);
	    Node node = nodes.item(0);
	    return node.getChildNodes().item(0).getNodeValue();
	}
	
	/**
	 * set the node value, only applicable for names that only have one node entry
	 * @param nodeName, node name
	 * @param nodeValue, node value to be set
	 * @throws Exception
	 */
	public void setNodeValue(String nodeName, String nodeValue) throws Exception{
		if(doc==null) {
			init();
		}
	    
	    NodeList nodes = doc.getElementsByTagName(nodeName);
	    Node node = nodes.item(0);
	    node.getChildNodes().item(0).setNodeValue(nodeValue);
	}
	
	/**
	 * get the href url of the specific node
	 * @param nodeName, the node name
	 * @return
	 * @throws Exception
	 */
	public List<String> getURL(String nodeName) throws Exception {
		List<String> URLs = new ArrayList<String>();
		
		if(doc==null) {
			init();
		}
		
		NodeList nodes = doc.getElementsByTagName(nodeName);
		for(int i=0; i<nodes.getLength(); i++) {
			Node node = nodes.item(i);
			NodeList children = node.getChildNodes();
			for(int j=0; j<children.getLength(); j++) {
				Node n = children.item(j);
				if(n.getNodeName().equalsIgnoreCase(HMCConstants.XML_LINK)){
					NamedNodeMap attrMap = n.getAttributes();
					String value = attrMap.getNamedItem(HMCConstants.XML_LINK_REF).getNodeValue();
					URLs.add(value);
				}
			}
		}
		return URLs;
	}
	
	/**
	 * get the location code of the network adapter
	 * @return
	 * @throws Exception
	 */
	public String getLocationCode() throws Exception {
		return getNodeValue(HMCConstants.XML_LOCATION_CODE);
	}
	
	/**
	 * get the location code of the physical network adapter
	 * @return
	 * @throws Exception
	 */
	public Set<String> getPhysicalEthernetAdapterLocationCode() throws Exception {
		Set<String> locationCodeList = new HashSet<String>();
		if(doc==null) {
			init();
		}
		
		NodeList FeatureCodesList = doc.getElementsByTagName("FeatureCodes");
		String slot = null;
		for(int i=0; i<FeatureCodesList.getLength(); i++) {
			Node featureCode = FeatureCodesList.item(i);
			String value = featureCode.getChildNodes().item(0).getNodeValue();
			if(Utilities.isEthernetAdapter(value, 1)
					||Utilities.isEthernetAdapter(value, 2)
					||Utilities.isEthernetAdapter(value, 4)) {
				Node AssociatedIOSlot = featureCode.getParentNode();
				
				NodeList AssociatedIOSlot_children = AssociatedIOSlot.getChildNodes();
				for(int j=0; j<AssociatedIOSlot_children.getLength(); j++) {
					Node RelatedIOAdapter = AssociatedIOSlot_children.item(j);
					if(!"RelatedIOAdapter".equalsIgnoreCase(RelatedIOAdapter.getNodeName())) {
						continue;
					}
					
					NodeList RelatedIOAdapter_children = RelatedIOAdapter.getChildNodes();
					for(int k=0; k<RelatedIOAdapter_children.getLength(); k++) {
						Node IOAdapter = RelatedIOAdapter_children.item(k);
						if(!"IOAdapter".equalsIgnoreCase(IOAdapter.getNodeName())) {
							continue;
						}
						NodeList IOAdapter_children = IOAdapter.getChildNodes();
						for(int m=0; m<IOAdapter_children.getLength(); m++) {
							Node DeviceName = IOAdapter_children.item(m);
							if(!"DeviceName".equalsIgnoreCase(DeviceName.getNodeName())) {
								continue;
							}
							slot = DeviceName.getChildNodes().item(0).getNodeValue();
							
							//guess the port number
							if(Utilities.isEthernetAdapter(value, 4)) {
								locationCodeList.add(slot+"-T1");
								locationCodeList.add(slot+"-T2");
								locationCodeList.add(slot+"-T3");
								locationCodeList.add(slot+"-T4");
							}else if(Utilities.isEthernetAdapter(value, 2)){
								locationCodeList.add(slot+"-T1");
								locationCodeList.add(slot+"-T2");
							}else {
								locationCodeList.add(slot+"-T1");
							}
						}
					}
				}
				
			}
			
		}
		return locationCodeList;
	}
	
	/**
	 * get the location code of the physical RoCE adapter
	 * @return
	 * @throws Exception
	 */
	public Set<String> getPhysicalRoCEAdapterLocationCode() throws Exception {
		Set<String> locationCodeList = new HashSet<String>();
		if(doc==null) {
			init();
		}
		
		NodeList FeatureCodesList = doc.getElementsByTagName("FeatureCodes");
		String slot = null;
		for(int i=0; i<FeatureCodesList.getLength(); i++) {
			Node featureCode = FeatureCodesList.item(i);
			String value = featureCode.getChildNodes().item(0).getNodeValue();
			if(Utilities.isRoCEAdapter(value, 1)
					||Utilities.isRoCEAdapter(value, 2)
					||Utilities.isRoCEAdapter(value, 4)) {
				Node AssociatedIOSlot = featureCode.getParentNode();
				
				NodeList AssociatedIOSlot_children = AssociatedIOSlot.getChildNodes();
				for(int j=0; j<AssociatedIOSlot_children.getLength(); j++) {
					Node RelatedIOAdapter = AssociatedIOSlot_children.item(j);
					if(!"RelatedIOAdapter".equalsIgnoreCase(RelatedIOAdapter.getNodeName())) {
						continue;
					}
					
					NodeList RelatedIOAdapter_children = RelatedIOAdapter.getChildNodes();
					for(int k=0; k<RelatedIOAdapter_children.getLength(); k++) {
						Node IOAdapter = RelatedIOAdapter_children.item(k);
						if(!"IOAdapter".equalsIgnoreCase(IOAdapter.getNodeName())) {
							continue;
						}
						NodeList IOAdapter_children = IOAdapter.getChildNodes();
						for(int m=0; m<IOAdapter_children.getLength(); m++) {
							Node DeviceName = IOAdapter_children.item(m);
							if(!"DeviceName".equalsIgnoreCase(DeviceName.getNodeName())) {
								continue;
							}
							slot = DeviceName.getChildNodes().item(0).getNodeValue();
							
							//guess the port number
							if(Utilities.isRoCEAdapter(value, 4)) {
								locationCodeList.add(slot+"-T1");
								locationCodeList.add(slot+"-T2");
								locationCodeList.add(slot+"-T3");
								locationCodeList.add(slot+"-T4");
							}else if(Utilities.isRoCEAdapter(value, 2)){
								locationCodeList.add(slot+"-T1");
								locationCodeList.add(slot+"-T2");
							}else {
								locationCodeList.add(slot+"-T1");
							}
						}
					}
				}
				
			}
			
		}
		return locationCodeList;
	}

	
	public String DocumentToString() throws Exception {
		String output = null;
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        output = writer.getBuffer().toString();

        return output;
    }

}

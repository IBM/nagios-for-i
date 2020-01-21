package com.ibm.nagios.util;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ibm.nagios.model.Disk;

public class DiskXMLReader {
    private static ArrayList<Disk> diskList = new ArrayList<Disk>();

    synchronized public static ArrayList<Disk> getDiskConfigInfo(String inputXML) {
        diskList.clear();
        parseXML(inputXML);
        return diskList;
    }

    private static void parseXML(String inputXML) {
        try {
            DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder domParser = dbFact.newDocumentBuilder();
            InputSource isrc = new InputSource(new StringReader(inputXML));
            Document document = domParser.parse(isrc);
            NodeList disks = document.getElementsByTagName("Disk");
            for (int i = 0; i < disks.getLength(); i++) {
                Node diskNode = disks.item(i);
                if (diskNode != null && diskNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (diskNode.getAttributes().getNamedItem("ASP") != null) {
                        String ASPNum = diskNode.getAttributes().getNamedItem("ASP").getNodeValue();
                        String unitNum = diskNode.getAttributes().getNamedItem("Number").getNodeValue();
                        String resourceName = diskNode.getAttributes().getNamedItem("Name").getNodeValue();
                        String status = diskNode.getAttributes().getNamedItem("Status").getNodeValue();

                        Disk disk = new Disk();
                        disk.setASPNum(Integer.parseInt(ASPNum));
                        disk.setDiskNum(Integer.parseInt(unitNum));
                        disk.setResourceName(resourceName);
                        disk.setStatus(Integer.parseInt(status));
                        diskList.add(disk);
                    }

                }
            }
        } catch (Exception e) {
            System.err.println("Error in parsing disk xml: " + e.toString());
        }
    }
}

package com.ibm.nagios.hmc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Utilities {

	public static boolean pingPort(String host, int port, int timeout) {
    	boolean reachable = false;
    	
		Socket soc = new Socket();
		SocketAddress add = new InetSocketAddress(host, port);
		try {
			soc.connect(add, timeout);
			reachable = true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				soc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	return reachable;
    }
	
	public static String setTrustedStorePath() {
		//String oldTrustStorePath = System.getProperty("javax.net.ssl.trustStore");
		String newTrustStorePath = null;
		newTrustStorePath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "jssecacerts";
		//set the new path
		return System.setProperty("javax.net.ssl.trustStore", newTrustStorePath);
	}
	
	public static void rollbackTrustedStorePath(String oldTrustStorePath) {
		if(oldTrustStorePath == null) {
			System.clearProperty("javax.net.ssl.trustStore");
		}else {
			System.setProperty("javax.net.ssl.trustStore", oldTrustStorePath);
		}
	}
	
	public static InputStream loadResource(String path) {
    	InputStream is = null;
    	if(path!=null && !path.isEmpty()) {
    		is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    		if(is == null) {
    		    is = Utilities.class.getClassLoader().getResourceAsStream(path);
    		}
    		if(is == null) {
    			is = ClassLoader.getSystemResourceAsStream(path);
    		}
    	}
    	return is;
    }
	
	public static String getXmlAsString(InputStream in) throws Exception {
		BufferedReader ibf = new BufferedReader(
		        new InputStreamReader(in));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = ibf.readLine()) != null) {
			response.append(inputLine);
		}

		return response.toString();
	}
	
	public static boolean isRoCEAdapter(String FeatureCode, int port) {
		boolean is = false;
		if(FeatureCode==null || FeatureCode.isEmpty()) {
			return is;
		}

		String [] FeatureCodesList =null;
		switch(port) {
			case 1:
				FeatureCodesList = FeatureCodesList1_roce;
				break;
			case 2:
				FeatureCodesList = FeatureCodesList2_roce;
				break;
			case 4:
				FeatureCodesList = FeatureCodesList4_roce;
				break;
			default:
				FeatureCodesList = FeatureCodesList4_roce;
		}

		for(String fc : FeatureCodesList) {
			if(FeatureCode.equalsIgnoreCase(fc)) {
				is = true;
				break;
			}
		}
		return is;
	}
	
	public static final String[] FeatureCodesList2_roce = new String [] {
			//RoCE
			"EC2M",//PCIe3 2-Port 10 GbE NIC and RoCE SR Adapter
			"EC2N",
			"EL40",
			"EC37",//PCIe3 2-port 10 GbE NIC and RoCE SFP+ Copper Adapter
			"EC38",
			"EC3X",
			"EC3A",//PCIe3 2-port 40GbE NIC RoCE QSFP+ Adapter
			"EC3B",
			"EC27",//PCIe2 LP 2-port 10GbE RoCE SFP+ Adapter (NIC Only)
			"EC28",
			"EC29",
			"EC30",
			"EC3L",//2-Port 100Gb RoCE QSFP28 PCIe 3.0 x16 Adapter
			"EC3M",
			"EL27",//PCIe2 LP 2-Port 10GbE RoCE SFP+ adapter
			"EL2Z",//PCIe2 LP 2-Port 10 GbE RoCE SR adapter
			"EL3X",//PCIe3 2-Port 10 GbE NIC and RoCE SR adapter
			"EL53",//PCIe3 2-Port 10 GbE NIC and RoCE SR adapter
			"EL54",//PCIe3 2-port 10 GbE NIC and RoCE SR adapter
			"EC2R",//PCIe3 2-port 10 Gb NIC & RoCE SR/Cu adapter
			"EC2S",
			"EC2T",//PCIe3 2-port 25/10 Gb NIC & RoCE SFP28 adapter
			"EC2U",
			"EC3L",//PCIe3 2-port 100 GbE NIC & RoCE QSFP28 Adapter
			"EC3M",
			"EC66",//PCIe4 2-port 100 GbE RoCE x16 adapter 
			"EC67",
	};
	
	public static final String[] FeatureCodesList1_roce = new String [] {

	};
	
	public static final String[] FeatureCodesList4_roce = new String [] {

	};
	
	public static boolean isEthernetAdapter(String FeatureCode, int port) {
		boolean is = false;
		if(FeatureCode==null || FeatureCode.isEmpty()) {
			return is;
		}

		String [] FeatureCodesList =null;
		switch(port) {
			case 1:
				FeatureCodesList = FeatureCodesList1;
				break;
			case 2:
				FeatureCodesList = FeatureCodesList2;
				break;
			case 4:
				FeatureCodesList = FeatureCodesList4;
				break;
			default:
				FeatureCodesList = FeatureCodesList4;
		}

		for(String fc : FeatureCodesList) {
			if(FeatureCode.equalsIgnoreCase(fc)) {
				is = true;
				break;
			}
		}
		return is;
	}
	
	//https://www.ibm.com/support/knowledgecenter/en/POWER8/p8hcd/p8hcd_pcibyfeature_list_kickoff_pdf.htm
		public static final String[] FeatureCodesList4 = new String [] {
				"EN15",//PCIe3 4-port 10 GbE SR Adapter
				"EN16",
				"EN17",//PCIe3 4-port 10 GbE SFP+ Copper Adapter
				"EN18",
				"EN0S",//PCIe2 4-port (10Gb+1GbE) SR+RJ45 Adapter
				"EN0T",
				"EN0U",//PCIe2 4-port (10Gb+1GbE) Coper SFP+RJ45 Adapter
				"EN0V",
				"5260",//PCIe2 4-port 1GbE Adapter
				"5899",
				"0637",//PCI 10/100Mbps Ethernet UTP 4-port
				"5740",//PCI-X 10/100/1000Mbps Ethernet 4-port
				"5624",//4-Port 1Gb Integrated Virtual Ethernet Daughter Card
				"5717",//4-Port 10/100/1000 Base-TX PCI Express Adapter
				"5271",

		};

		public static final String[] FeatureCodesList2 = new String [] {
				"EN0W",//PCIe2 2-port 10GbE BaseT RJ45 Adapter
				"EN0X",
				"EL3Z",
				"5274",//PCIe 2-port 10GbE SX Adapter
				"5281",//PCIe 2-port 1GbE TX Adapter
				"5284",//PCIe2 2-port 10GbE Adapter
				"5287",
				"5286",
				"5706",//PCI 10/100/1000Mbps Ethernet UTP 2-port
				"5707",//PCI 1Gbps Ethernet Fiber 2-port
				"5767",//PCI-E4x 1 Gb Ethernet Copper 2 port
				"5768",//PCI-E4x 1 Gb Ethernet Fiber 2 port
				"5613",//Dual Port (SR) Integrated Virtual Ethernet 10Gb Daughter Card
				"5623",//Dual Port 1Gb Integrated Virtual Ethernet Daughter Card
				"EN22",//2-Port 10Gb Fibre Channel over Ethernet (FC0E) or Converged Network Adapter

				//RoCE
//				"EC2M",//PCIe3 2-Port 10 GbE NIC and RoCE SR Adapter
//				"EC2N",
//				"EL40",
//				"EC37",//PCIe3 2-port 10 GbE NIC and RoCE SFP+ Copper Adapter
//				"EC38",
//				"EC3X",
//				"EC3A",//PCIe3 2-port 40GbE NIC RoCE QSFP+ Adapter
//				"EC3B",
//				"EC27",//PCIe2 LP 2-port 10GbE RoCE SFP+ Adapter (NIC Only)
//				"EC28",
//				"EC29",
//				"EC30",
//				"EC3L",//2-Port 100Gb RoCE QSFP28 PCIe 3.0 x16 Adapter
//				"EC3M",
//				"EL27",//PCIe2 LP 2-Port 10GbE RoCE SFP+ adapter
//				"EL2Z",//PCIe2 LP 2-Port 10 GbE RoCE SR adapter
//				"EL3X",//PCIe3 2-Port 10 GbE NIC and RoCE SR adapter
//				"EL53",//PCIe3 2-Port 10 GbE NIC and RoCE SR adapter
//				"EL54",//PCIe3 2-port 10 GbE NIC and RoCE SR adapter
//				"EC2R",//PCIe3 2-port 10 Gb NIC & RoCE SR/Cu adapter
//				"EC2S",
//				"EC2T",//PCIe3 2-port 25/10 Gb NIC & RoCE SFP28 adapter
//				"EC2U",
//				"EC3L",//PCIe3 2-port 100 GbE NIC & RoCE QSFP28 Adapter
//				"EC3M",
//				"EC66",//PCIe4 2-port 100 GbE RoCE x16 adapter 
//				"EC67",
		};

		public static final String[] FeatureCodesList1 = new String [] {
				"5275",//PCIe 10GbE SR 1-port Adapter
				"EN0H",//PCIe2 2x10Gb FCoE 2x1GbE SFP+ Adapter
				"EN0J",
				"0620",//PCI 1Gbps Ethernet IOA
				"0621",//PCI 1Gbps Ethernet UTP IOA
				"4962", //PCI 10/100Mbps Ethernet w/ IPSec
				"5718",//PCI 10Gbps Ethernet Fiber
				"5719",//PCI 10Gbps Ethernet Fiber
				"5721",//PCI-xddr 10Gbps Ethernet Fiber
				"5722",//PCI-xddr 10Gbps Ethernet Fiber
				"5636",//Integrated, 2X- 1Gb Virtual Ethernet, I/O ports
				"5637",//Integrated, 2X- 10Gb (SR) Virtual Ethernet, I/O ports
				"5639",//Integrated, 4X- 1Gb Virtual Ethernet, I/O ports
				"EN10",//Integrated Multi-Function Card
				"EN11",
				"5772",//10 Gigabit Ethernet-LR PCI Express Adapter

		};
}

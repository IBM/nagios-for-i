package com.ibm.nagios.hmc;

import java.util.List;

public class HMCBean {
	public String commonName = null;
	public List<String> keys = null;
	public String restAPI = null;
	
	public HMCBean(String commonName, List<String> keys, String restAPI) {
		this.commonName = commonName;
		this.keys = keys;
		this.restAPI = restAPI;
	}
}

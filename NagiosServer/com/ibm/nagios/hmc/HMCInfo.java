package com.ibm.nagios.hmc;

public class HMCInfo {
    public String system;
    public String port;
    public String ksPassword;
    public String user;
    public String password;

    public HMCInfo(String system, String port, String ksPassword, String user, String password) {
        this.system = system;
        this.port = port;
        this.ksPassword = ksPassword;
        this.user = user;
        this.password = password;
    }
    
    @Override
    public String toString() {
        return String.format(this.system + this.user);
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getKsPassword() {
        return ksPassword;
    }

    public void setKsPassword(String ksPassword) {
        this.ksPassword = ksPassword;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

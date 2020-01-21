package com.ibm.nagios.config.util;

import java.io.Serializable;


public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String user;
    private String password;

    public UserInfo(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}

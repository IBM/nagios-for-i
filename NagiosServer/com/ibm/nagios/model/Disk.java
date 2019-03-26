package com.ibm.nagios.model;

public class Disk {
    private int aNum;
    private int dNum;
    private Double percBusy;
    private String percUsed;
    private int numReads;
    private int numWrites;
    private int totalIO;
    private int capacity;
    private int availCap;
    private String type;
    
    private int status;
    private String modle;
    private String resourceName;

    public Disk() {

    }

    public void setModle(String modle) {
    	this.modle = modle;
    }

    public String getModle() {
        return modle;
    }
    
    public void setResourceName(String resourceName) {
    	this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }
    
    public void setASPNum(int num) {
        aNum = num;
    }

    public int getASPNum() {
        return aNum;
    }

    public void setDiskNum(int num) {
        dNum = num;
    }

    public int getDiskNum() {
        return dNum;
    }

    public void setDiskBusy(Double busy) {
        percBusy = busy;
    }

    public Double getDiskBusy() {
        return percBusy;
    }

    public void setDiskUsed(String used) {
        percUsed = used;
    }

    public String getDiskUsed() {
        return percUsed;
    }

    public void setDiskReads(int readReq) {
        numReads = readReq;
    }

    public int getDiskReads() {
        return numReads;
    }

    public void setDiskWrites(int writeReq) {
        numWrites = writeReq;
    }

    public int getDiskWrites() {
        return numWrites;
    }

    public void setDiskTotalIO(int totalReq) {
        totalIO = totalReq;
    }

    public int getDiskTotalIO() {
        return totalIO;
    }

    public void setDiskCapacity(int diskCapacity) {
        capacity = diskCapacity;
    }

    public int getDiskCapacity() {
        return capacity;
    }

    public void setDiskAvailable(int available) {
        availCap = available;
    }

    public int getDiskAvailable() {
        return availCap;
    }

    public void setDiskType(String diskType) {
        type = diskType;
    }

    public String getDiskType() {
        return type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

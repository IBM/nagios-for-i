package com.ibm.nagios.model;

import java.util.ArrayList;

/*
 * unused since the design change
 */
public class ASP {
    private int aNum;
    private float percUsed;
    private ArrayList <Disk> disks = new ArrayList<Disk>();
    
    public ASP() {
        
    }
    
    public void setASPNum(int num) {
        aNum = num;
    }
    
    public int getASPNum() {
        return aNum;
    }
    
    public void setASPPercUsed(float used) {
        percUsed = used;
    }
    
    public float getASPPercUsed() {
        return percUsed;
    }
    
    public void addDisk(Disk newDisk) {
        int newDiskNum = newDisk.getDiskNum();
        boolean added = false;
        
        for (int i = 0; i < disks.size(); i++) {
            int tempDiskNum = disks.get(i).getDiskNum();
            
            if (newDiskNum == tempDiskNum || newDiskNum < tempDiskNum) {
                disks.add(i, newDisk);
                added = true;
                break;
            }
        }
        if (!added) {
            disks.add(newDisk);
        }
    }
    
    public ArrayList<Disk> getDisks() {
        return disks;
    }
}

package com.ibm.nagios.util;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.as400.access.AS400;
import com.ibm.nagios.model.Disk;

public class DiskUsageStatus {
    private static ConcurrentHashMap<String, DiskUsageStatus> diskStatus = new ConcurrentHashMap<String, DiskUsageStatus>();
    private ArrayList<Integer> sCountBase = new ArrayList<Integer>();
    private ArrayList<Integer> nbCountBase = new ArrayList<Integer>();
    private ArrayList<Integer> rReqBase = new ArrayList<Integer>();
    private ArrayList<Integer> wReqBase = new ArrayList<Integer>();
    private ArrayList<Disk> Disks = new ArrayList<Disk>();
    private boolean firstTime = true;
    private long currentTime; // Current time at last update
    private long totalTime; // Baseline which to determine the number of seconds

    private AS400 as400 = null;

    public DiskUsageStatus(AS400 as400) {
        this.as400 = as400;
    }

    public static DiskUsageStatus getInstance(AS400 as400) {
        if (as400 == null) {
            throw new NullPointerException("AS400 cannot be null");
        }
        DiskUsageStatus instance = diskStatus.get(as400.getSystemName());
        if (instance == null) {
            instance = new DiskUsageStatus(as400);
            diskStatus.put(as400.getSystemName(), instance);
        }
        return instance;
    }

    public void setASPDiskData() throws Exception {
        QYASPOL pc = new QYASPOL();
        Disks.clear();
        if (callQYASPOL(pc)) {
            if (!firstTime) {
                for (int i = 0; i < pc.getRcdsReturned(); i++) {
                    Disk disk = new Disk();
                    disk.setASPNum(pc.getASPNumber(i));
                    disk.setDiskNum(pc.getDiskNumber(i));
                    disk.setDiskBusy(pc.getPercBusy(i, sCountBase.get(i), nbCountBase.get(i)));
                    disk.setDiskUsed(pc.getPercentUsed(i));
                    disk.setDiskReads(pc.getReadRequests(i, rReqBase.get(i)));
                    disk.setDiskWrites(pc.getWriteRequests(i, wReqBase.get(i)));
                    disk.setDiskTotalIO(pc.getTotalIO(i, rReqBase.get(i), wReqBase.get(i)));
                    disk.setDiskCapacity(pc.getDiskCapacity(i));
                    disk.setDiskAvailable(pc.getDiskAvailable(i));
                    disk.setDiskType(pc.getDiskType(i));
                    Disks.add(disk);
                }
            } else {
                sCountBase = pc.getSCountBase();
                nbCountBase = pc.getNBCountBase();
                rReqBase = pc.getReadReqBase();
                wReqBase = pc.getWriteReqBase();
                totalTime = System.currentTimeMillis();
            }
            firstTime = false;
        }
    }

    public ArrayList<Disk> getDiskData() {
        return Disks;
    }

    public Boolean callQYASPOL(QYASPOL pc) throws Exception {
        return pc.run(as400, firstTime);
    }

    public long getSeconds() {
        currentTime = System.currentTimeMillis();
        return (currentTime - totalTime) / 1000;
    }
}

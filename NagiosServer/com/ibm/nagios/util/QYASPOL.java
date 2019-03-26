package com.ibm.nagios.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.BinaryConverter;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;

public class QYASPOL {
    private final String programName_ = "/QSYS.LIB/QGY.LIB/QYASPOL.PGM";
    // private static final int receiverLength_ = 32768;
    private int rcdLength_;
    private final int listLength_ = 80;
    private final String formatName_ = "YASP0300";
    private ProgramParameter errorCode_ = new ProgramParameter(BinaryConverter.intToByteArray(0));

    private ProgramParameter[] parameters_ = new ProgramParameter[] { new ProgramParameter(), // receiver
                    new ProgramParameter(), // receiver length
                    new ProgramParameter(listLength_), // list info
                    new ProgramParameter(BinaryConverter.intToByteArray(-1)), // records to return
                    new ProgramParameter(BinaryConverter.intToByteArray(1)), // filter
                    new ProgramParameter(), // filter info
                    new ProgramParameter(), // format
                    errorCode_, // error code
                    new ProgramParameter() // sort info
    };

    private int rcdsReturned_ = 0;
    private byte[] rqsHandle_;
    private HashSet<Integer> aspNumSet = new HashSet<Integer>();
    private ArrayList<Integer> sCountBase = new ArrayList<Integer>();
    private ArrayList<Integer> nbCountBase = new ArrayList<Integer>();
    private ArrayList<Integer> sCountNew = new ArrayList<Integer>();
    private ArrayList<Integer> nbCountNew = new ArrayList<Integer>();
    private ArrayList<Integer> aspNumber = new ArrayList<Integer>();
    private ArrayList<Integer> diskNumber = new ArrayList<Integer>();
    private ArrayList<String> diskType = new ArrayList<String>();
    private ArrayList<Integer> diskCapacity = new ArrayList<Integer>();
    private ArrayList<Integer> rRequestsBase = new ArrayList<Integer>();
    private ArrayList<Integer> wRequestsBase = new ArrayList<Integer>();
    private ArrayList<Integer> rRequestsNew = new ArrayList<Integer>();
    private ArrayList<Integer> wRequestsNew = new ArrayList<Integer>();
    private ArrayList<Float> percUsed = new ArrayList<Float>();
    private ArrayList<Integer> diskAvailable = new ArrayList<Integer>();
    private ArrayList<String> aspProType = new ArrayList<String>();
    private ArrayList<String> unitStatus = new ArrayList<String>();
    private boolean debug = false;

    private static final ThreadLocal<DecimalFormat> df = new ThreadLocal<DecimalFormat>() {
        protected DecimalFormat initialValue() {
            return new DecimalFormat("0.00");
        }
    };

    public QYASPOL() {

    }

    public final boolean run(AS400 system, boolean firstTime) throws Exception {
        int receiverLength = getPCLength(system);
        return collectData(system, firstTime, receiverLength);
    }

    private final int getPCLength(AS400 system) throws Exception {
    	AS400Text text8 = null;
    	AS400Text text4 = null;
    	byte[] filterInfo = null;
    	byte[] sortInfo = null;
        try {
            final ProgramCall pgm = new ProgramCall(system, programName_, parameters_);
            final int ccsid = system.getCcsid();
            text8 = new AS400Text(8, ccsid, system);
            text4 = new AS400Text(4, ccsid, system);
            filterInfo = new byte[16];
            BinaryConverter.intToByteArray(16, filterInfo, 0);
            BinaryConverter.intToByteArray(1, filterInfo, 4);
            BinaryConverter.intToByteArray(4, filterInfo, 8);
            BinaryConverter.intToByteArray(-1, filterInfo, 12);

            sortInfo = new byte[16];
            BinaryConverter.intToByteArray(16, filterInfo, 0);
            BinaryConverter.intToByteArray(1, filterInfo, 4);
            BinaryConverter.intToByteArray(4, filterInfo, 8);
            BinaryConverter.intToByteArray(-1, filterInfo, 12);

            parameters_[0].setOutputDataLength(0);
            parameters_[1].setInputData(BinaryConverter.intToByteArray(0));
            parameters_[5].setInputData(filterInfo);
            parameters_[6].setInputData(text8.toBytes(formatName_));
            parameters_[8].setInputData(sortInfo);
                
            if (!pgm.run()) {
            	throw new AS400Exception(pgm.getMessageList());
            }
            final byte[] listInfo = parameters_[2].getOutputData();
            rcdsReturned_ = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(listInfo, 4)), 0);
            rqsHandle_ = new byte[4];
            System.arraycopy(listInfo, 8, rqsHandle_, 0, 4);

            int totalRcds = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(listInfo, 0)), 0);
            rcdLength_ = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(listInfo, 12)), 0);
            int receiverLength = totalRcds * rcdLength_;

            ProgramParameter[] parameters = new ProgramParameter[] { new ProgramParameter(rqsHandle_), new ProgramParameter(BinaryConverter.intToByteArray(0)) };
            ProgramCall pc = new ProgramCall(system, "/QSYS.LIB/QGY.LIB/QGYCLST.PGM", parameters);
            if (!pc.run()) {
                throw new AS400Exception(pc.getMessageList());
            }
            return receiverLength;
        } catch (Exception e) {
            System.err.println("QYASPOL300.collectData: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("QYASPOL300.getPCLength: " + e.getMessage());
        } finally {
        	text8 = null;
        	text4 = null;
        	filterInfo = null;
        	sortInfo = null;
        }
    }

    /*
     * Performs the program call using the qyaspol API to gather the disk information. Adds gathered data to the
     * appropriate list The number of records returned is 1 for each disk within the system
     */
    private final boolean collectData(AS400 system, boolean firstTime, int receiverLength) throws Exception {
    	AS400Text text8 = null;
    	AS400Text text4 = null;
    	byte[] filterInfo = null;
    	byte[] sortInfo = null;
    	byte[] data = null;
    	byte[] listInfo = null;
        try {
            final ProgramCall pgm = new ProgramCall(system, programName_, parameters_);
            final int ccsid = system.getCcsid();
            text8 = new AS400Text(8, ccsid, system);
            text4 = new AS400Text(4, ccsid, system);
            filterInfo = new byte[16];
            BinaryConverter.intToByteArray(16, filterInfo, 0);
            BinaryConverter.intToByteArray(1, filterInfo, 4);
            BinaryConverter.intToByteArray(4, filterInfo, 8);
            BinaryConverter.intToByteArray(-1, filterInfo, 12);

            sortInfo = new byte[16];
            BinaryConverter.intToByteArray(16, filterInfo, 0);
            BinaryConverter.intToByteArray(1, filterInfo, 4);
            BinaryConverter.intToByteArray(4, filterInfo, 8);
            BinaryConverter.intToByteArray(-1, filterInfo, 12);

            parameters_[0].setOutputDataLength(receiverLength);
            parameters_[1].setInputData(BinaryConverter.intToByteArray(receiverLength));
            parameters_[5].setInputData(filterInfo);
            parameters_[6].setInputData(text8.toBytes(formatName_));
            parameters_[8].setInputData(sortInfo);

            if (!pgm.run()) {
            	throw new AS400Exception(pgm.getMessageList());
            }

            data = parameters_[0].getOutputData();

            listInfo = parameters_[2].getOutputData();
            rcdsReturned_ = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(listInfo, 4)), 0);
            int totalRcds = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(listInfo, 0)), 0);
            int firstRecord = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(listInfo, 36)), 0);
            rqsHandle_ = new byte[4];
            System.arraycopy(listInfo, 8, rqsHandle_, 0, 4);
            if (debug) {
                System.out.println("QYASPOL300.collectData: Total Records:" + totalRcds);
                System.out.println("QYASPOL300.collectData: Records Returned:" + rcdsReturned_);
                System.out.println("QYASPOL300.collectData: First Record:" + firstRecord);
                if ((firstRecord + rcdsReturned_ - 1) != totalRcds) {
                    System.out.println("QYASPOL300.collectData: Not all records were returned");
                }
            }

            sCountNew.clear();
            nbCountNew.clear();
            rRequestsNew.clear();
            wRequestsNew.clear();
            aspNumber.clear();
            diskNumber.clear();
            diskType.clear();
            diskCapacity.clear();
            percUsed.clear();
            diskAvailable.clear();
            aspProType.clear();
            unitStatus.clear();

            if (!firstTime) {
                for (int i = 0; i < rcdsReturned_; i++) {

                    int offset = i * rcdLength_; // The receiver struct has a length of 96
                    // Sample Count -- 80
                    int sInt = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (80 + offset))), 0);
                    // Not Busy Count -- 84
                    int nbInt = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (84 + offset))), 0);
                    // ASP Number -- 0
                    int aspNum = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (0 + offset))), 0);
                    // Disk Number -- 32
                    int dNum = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (32 + offset))), 0);
                    // Read Requests -- 64
                    int rReq = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (64 + offset))), 0);
                    // Write Requests -- 68
                    int wReq = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (68 + offset))), 0);
                    // Disk Type -- 4
                    String dType = ((String) text4.toObject(data, (4 + offset))).trim();
                    // Disk Capacity -- 36
                    int dSize = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (36 + offset))), 0);
                    // Storage Available -- 40
                    int storAvail = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (40 + offset))), 0);
                    // Storage Reserved For System -- 44
                    int storReser = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (44 + offset))), 0);

                    // Baseline is established so now just update value
                    sCountNew.add(sInt);
                    nbCountNew.add(nbInt);
                    rRequestsNew.add(rReq);
                    wRequestsNew.add(wReq);

                    // No baseline needed so just gather data normally
                    aspNumSet.add(aspNum);
                    aspNumber.add(aspNum);
                    diskNumber.add(dNum);
                    diskType.add(dType);
                    diskCapacity.add(dSize);
                    diskAvailable.add(storAvail);
                    percUsed.add(calcPercentUsed(dSize, storAvail, storReser));
                }
            } else {
                for (int i = 0; i < rcdsReturned_; i++) {

                    int offset = i * 96; // The receiver struct has a length of 96
                    // Sample Count -- 80
                    int sInt = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (80 + offset))), 0);
                    // Not Busy Count -- 84
                    int nbInt = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (84 + offset))), 0);
                    // Read Requests -- 64
                    int rReq = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (64 + offset))), 0);
                    // Write Requests -- 68
                    int wReq = BinaryConverter.byteArrayToInt(text4.toBytes(text4.toObject(data, (68 + offset))), 0);

                    sCountBase.add(sInt);
                    nbCountBase.add(nbInt);

                    rRequestsBase.add(rReq);
                    wRequestsBase.add(wReq);
                }
            }
            
            ProgramParameter[] parameters = new ProgramParameter[] { new ProgramParameter(rqsHandle_), new ProgramParameter(BinaryConverter.intToByteArray(0)) };
            ProgramCall pc = new ProgramCall(system, "/QSYS.LIB/QGY.LIB/QGYCLST.PGM", parameters);
            if (!pc.run()) {
                throw new AS400Exception(pc.getMessageList());
            }
            return true;
        } catch (Exception e) {
            System.err.println("QYASPOL300.collectData: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("QYASPOL300.collectData: " + e.getMessage());
        } finally {
        	text8 = null;
        	text4 = null;
        	filterInfo = null;
        	sortInfo = null;
        	data = null;
        	listInfo = null;
        	rqsHandle_ = null;
        	errorCode_ = null;
        	parameters_ = null;
        }
    }

    public ArrayList<Integer> getReadReqBase() {
        return rRequestsBase;
    }

    public ArrayList<Integer> getWriteReqBase() {
        return wRequestsBase;
    }

    public ArrayList<Integer> getSCountBase() {
        return sCountBase;
    }

    public ArrayList<Integer> getNBCountBase() {
        return nbCountBase;
    }

    /*
     * The following methods return the element at the given index (unit number)
     */
    /*
     * public Integer getTotalIO(int i) { return totalIOReq.get(i); }
     * 
     * public Integer getReadRequests(int i) { return (rRequestsNew.get(i) - rRequestsBase.get(i)); }
     * 
     * public Integer getWriteRequests(int i) { return (wRequestsNew.get(i) - wRequestsBase.get(i)); }
     */

    public int getReadRequests(int i, Integer rRequestsBase) {
        return (rRequestsNew.get(i) - rRequestsBase);
    }

    public int getWriteRequests(int i, Integer wRequestsBase) {
        return (wRequestsNew.get(i) - wRequestsBase);
    }

    public int getAspCount() {
    	return aspNumSet.size();
    }
    
    public int getASPNumber(int i) {
        return aspNumber.get(i);
    }

    public int getDiskNumber(int i) {
        return diskNumber.get(i);
    }

    public int getDiskCapacity(int i) {
        return diskCapacity.get(i);
    }

    public int getDiskAvailable(int i) {
        return diskAvailable.get(i);
    }

    public String getDiskType(int i) {
        return diskType.get(i);
    }

    public String getPercentUsed(int i) {
        df.get().setRoundingMode(RoundingMode.CEILING);
        String temp = Float.toString(percUsed.get(i));
        return df.get().format(Double.parseDouble(temp));
    }

    public String getASPProType(int i) {
        return aspProType.get(i);
    }

    public String getUnitStatus(int i) {
        return unitStatus.get(i);
    }

    /*
     * Returns the number of records returned which is equivalent to the number of units
     */
    public int getRcdsReturned() {
        return rcdsReturned_;
    }

    /*
     * Calculates the percent of used space Inputs the size, available space, and reserved space returned from the
     * program call Outputs value as a percent % Percent Used = (Size - storAvail + storReser) / Size
     */
    private float calcPercentUsed(int aSize, int storAvail, int storReser) {
        if (aSize == 0) {
            return 0;
        }
        int storUsed = aSize - storAvail + storReser;
        float percUsed = 100 * ((float) storUsed / (float) aSize);
        return percUsed;
    }

    /*
     * Calculates the total I/O requests Time is not accounted for in this method Output is the total of reads + total
     * of writes
     */
    public int getTotalIO(int i, Integer rRequestsBase, Integer wRequestsBase) {
        int tempR = (rRequestsNew.get(i) - rRequestsBase);
        int tempW = (wRequestsNew.get(i) - wRequestsBase);
        int total = tempR + tempW;
        return total;
    }

    /*
     * Calculates the percent busy Percent Busy = (sample count - not busy count) / sample count Outputs value as a %
     */
    public double getPercBusy(int i, Integer sCountBase, Integer nbCountBase) {
        int sCnt = (sCountNew.get(i) - sCountBase);
        if (sCnt == 0) {
            return 0.;
        }
        int nbCnt = (nbCountNew.get(i) - nbCountBase);
        int bCnt = sCnt - nbCnt;
        double pBusy = (double) bCnt / (double) sCnt;
        return pBusy * 100;
    }
}
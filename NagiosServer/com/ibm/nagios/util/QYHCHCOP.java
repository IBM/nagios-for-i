package com.ibm.nagios.util;

import java.beans.PropertyVetoException;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.BinaryConverter;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;

public class QYHCHCOP {
	//EBCDIC CODE
	public final String EBCDIC_CODE_PAGE="Cp037";
	  
	private final String programName_ = "/QSYS.LIB/QYHCHCOP.PGM";
	private final String requestInfo = "<DMConfigurationScript DTDVersion=\"50\"><Info><DiskInfo Format=\"All\"><DiskFilter Value=\"All\"/></DiskInfo></Info></DMConfigurationScript>";
	private final int OUTPUT_LENGTH = 10000;

	public final int REQUEST      = 0;
	public final int REQUEST_LEN  = 1;
	public final int REQUEST_TYPE = 2;
	public final int RECEIVER     = 3;
	public final int RECEIVER_LEN = 4;
	public final int RETURN_DATA  = 5;
	public final int USER_ID      = 7;
	public final int USER_ID_LEN  = 8;
	public final int PASSWORD     = 9;
	public final int PASSWOD_LEN  = 10;
	public final int CCSID        = 11;
	
    private ProgramParameter errorCode_ = new ProgramParameter(BinaryConverter.intToByteArray(0));

    private ProgramParameter[] parameters_ = new ProgramParameter[] { 
    	new ProgramParameter(), // Hardware configuration request
        new ProgramParameter(), // Hardware configuration request length
        new ProgramParameter(), // Request type
        new ProgramParameter(), // Receiver variable
        new ProgramParameter(), // Length of receiver variable
        new ProgramParameter(), // Bytes of return data available
        errorCode_, 			// Error code
        new ProgramParameter(), // Requesting service tools user ID
        new ProgramParameter(), // Length of requesting service tools user ID
        new ProgramParameter(), // Requesting service tools user ID password
        new ProgramParameter(), // Length of requesting service tools user ID password
        new ProgramParameter(), // CCSID of requesting service tools user ID password
    };

    public QYHCHCOP() {

    }

    public final String run(AS400 system, StringBuffer response) throws Exception {
    	final ProgramCall pgm = new ProgramCall(system, programName_, parameters_);
    	init(system);
        
        if (!pgm.run()) {
        	AS400Message msgList[] = pgm.getMessageList();
        	StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        	response.append(Constants.retrieveDataError + " - ");
        	for(int i=0; i<msgList.length; i++) {
        		String msgID = msgList[i].getID();
        		String errMsg = msgList[i].getText();
        		if(msgID.equalsIgnoreCase("MCH1001") && errMsg.equalsIgnoreCase("Attempt to use permanent system object QYHCHCOP without authority.")) {
        			errMsg = "Service Disk configuration needs authorities of *ALLOBJ, *SERVICE and *IOSYSCFG";
        		}
        		response.append(msgID + ": " + errMsg + "\n");
        	}
        	CommonUtil.printStack(stackTrace, response);
            return null;
        }
        String receiver = new String(parameters_[3].getOutputData(), EBCDIC_CODE_PAGE);
//        byte[] EBCDIC_string = reponse.getBytes(EBCDIC_CODE_PAGE);
//        response = new String(EBCDIC_string,EBCDIC_CODE_PAGE);
        int returnData = BinaryConverter.byteArrayToInt(parameters_[5].getOutputData(), 0);
        if(returnData > OUTPUT_LENGTH) {
        	// call again
        	parameters_[RECEIVER].setOutputDataLength(returnData);
            parameters_[RECEIVER_LEN].setInputData(BinaryConverter.intToByteArray(returnData));
            if (!pgm.run()) {
            	AS400Message msgList[] = pgm.getMessageList();
            	StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            	response.append(Constants.retrieveDataError + " - " + stackTrace[0].toString());
            	for(int i=0; i<msgList.length; i++) {
            		response.append(msgList[i].getID() + ": " + msgList[i].getText() + "\n");
            	}
            	CommonUtil.printStack(stackTrace, response);
                return null;
            }
            else {
            	receiver = new String(parameters_[3].getOutputData(), EBCDIC_CODE_PAGE);
            }
        }
        errorCode_ = null;
        parameters_ = null;
        return receiver;
    }
    
    public void setParameter(int index, byte[] input) throws PropertyVetoException {
    	parameters_[index].setInputData(input);
    }
    
    private final void init(AS400 system) throws Exception {
    	AS400Text reqText = null;
        try {
        	final int ccsid = system.getCcsid();
        	int requestLen = requestInfo.length();
            reqText = new AS400Text(requestLen, ccsid, system);
            
            parameters_[REQUEST].setInputData(reqText.toBytes(requestInfo));
            parameters_[REQUEST_LEN].setInputData(BinaryConverter.intToByteArray(requestInfo.length()));
            parameters_[REQUEST_TYPE].setInputData(BinaryConverter.intToByteArray(2));
            parameters_[RECEIVER].setOutputDataLength(OUTPUT_LENGTH);
            parameters_[RECEIVER_LEN].setInputData(BinaryConverter.intToByteArray(OUTPUT_LENGTH));
            parameters_[RETURN_DATA].setOutputDataLength(4);
            parameters_[CCSID].setInputData(BinaryConverter.intToByteArray(0));
            
        } catch (PropertyVetoException pve) {
            System.err.println("QYHCHCOP.init: Error setting parameters: " + pve.toString());
        } finally {
        	reqText = null;
        }
    }
}

package com.ibm.nagios.util;

import java.beans.PropertyVetoException;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.BinaryConverter;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;

public class QWCRSSTS {
	private final String programName_ = "/QSYS.LIB/QWCRSSTS.PGM";
	private final String format = "SSTS0100";
	private final String resetStsStat = "*YES      ";
	private final int receiverLength = 200;
	
	public final int RECEIVER       = 0;
	public final int RECEIVER_LEN   = 1;
	public final int FORMAT  	   = 2;
	public final int RESET_STS_STAT = 3;
	
    private ProgramParameter errorCode_ = new ProgramParameter(BinaryConverter.intToByteArray(0));

    private ProgramParameter[] parameters_ = new ProgramParameter[] {
    	new ProgramParameter(), // Receiver variable
        new ProgramParameter(), // Length of receiver variable
        new ProgramParameter(), // Format name
        new ProgramParameter(), // Reset status statistics
        errorCode_ 			// Error code
    };

    public QWCRSSTS() {

    }

    public final byte[] run(AS400 system, StringBuffer response) throws Exception {
    	final ProgramCall pgm = new ProgramCall(system, programName_, parameters_);
    	init(system);
        
        if (!pgm.run()) {
        	AS400Message msgList[] = pgm.getMessageList(); 
        	response.append(Constants.retrieveDataError + " - ");
        	for(int i=0; i<msgList.length; i++) {
        		response.append(msgList[i].getID() + ": " + msgList[i].getText() + "\n");
        	}
            return null;
        }
        byte[] receiver = parameters_[0].getOutputData().clone();
        errorCode_ = null;
        parameters_ = null;
        return receiver;
    }
    
    public void setParameter(int index, byte[] input) throws PropertyVetoException {
    	parameters_[index].setInputData(input);
    }
    
    private final void init(AS400 system) throws Exception {
    	AS400Text formatText = null;
    	AS400Text resetStsStatText = null;
        try {
        	final int ccsid = system.getCcsid();
            formatText = new AS400Text(format.length(), ccsid, system);
            resetStsStatText = new AS400Text(resetStsStat.length(), ccsid, system);
            
            parameters_[RECEIVER].setOutputDataLength(receiverLength);
            parameters_[RECEIVER_LEN].setInputData(BinaryConverter.intToByteArray(receiverLength));
            parameters_[FORMAT].setInputData(formatText.toBytes(format));
            parameters_[RESET_STS_STAT].setInputData(resetStsStatText.toBytes(resetStsStat));
        } catch (PropertyVetoException pve) {
            System.err.println("QWCRSSTS.init: Error setting parameters: " + pve.toString());
        } finally {
        	formatText = null;
        	resetStsStatText = null;
        }
    }
}

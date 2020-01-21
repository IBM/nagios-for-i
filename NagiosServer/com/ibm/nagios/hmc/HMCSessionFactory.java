package com.ibm.nagios.hmc;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.nagios.hmc.xml.XmlParser;


public class HMCSessionFactory {
    public final static String HMC_PORT = "12443";
    public final static String HMC_REST_ROOT_CONTEXT_PATH = "/rest/api/uom";
    public final static String HMC_REST_SCHEMA = "/rest/api/web/schema";
    public final static String HMC_REST_SEARCH = "/search";
    public final static String HMC_REST_QUICK = "/quick";
    public final static String HMC_REST_QUICK_ALL = "/quick/all";
    public final static String HMC_REST_MANAGED_SYSTEM = "ManagedSystem";
    public final static String HMC_REST_LOGICAL_PARTITION = "LogicalPartition";
    public static final String LOGON_CONTEXT = "/rest/api/web/Logon";
    public static final String LOGON_XML_PATH = "com/ibm/nagios/hmc/logon.xml";

    public static final String PORT = ":" + HMC_PORT;
    public static final String HTTPS = "https://";

    public static final String LOGON_MIME_TYPE = "application/vnd.ibm.powervm.web+xml";

    public static ConcurrentHashMap<String, String> sessions = new ConcurrentHashMap<>();

    public static String getSession(HMCInfo hmcInfo) throws Exception {
        if (sessions.get(hmcInfo.toString()) == null) {
            logonHMC(hmcInfo);
        }
        return sessions.get(hmcInfo.toString());
    }

    private static void logonHMC(HMCInfo hmcInfo) throws Exception {
        String logonURL = HTTPS + hmcInfo.system + PORT + LOGON_CONTEXT;
        InputStream is = Utilities.loadResource(LOGON_XML_PATH);
        String body = Utilities.getXmlAsString(is);
        body = replaceUserAndPassword(body, hmcInfo.user, hmcInfo.password);
        //@todo, add IBM i key management support
        //String key = args.getKey();

        InputStream response_is = null;
        response_is = HTTPRequestUtilities.sendPutRequest(logonURL, LOGON_MIME_TYPE, null, body);

        XmlParser xmlparser = new XmlParser(response_is);
        String session = xmlparser.getXAPISession();
        sessions.put(hmcInfo.toString(), session);
    }

    private static String replaceUserAndPassword(String str, String user, String password) throws Exception {
        //String key = args.getKey();
        if (str != null) {
            str = str.replace(HMCConstants.USER_ID_KEY, user);
            str = str.replace(HMCConstants.PASSWORD_KEY, password);
        }
        return str;
    }
}

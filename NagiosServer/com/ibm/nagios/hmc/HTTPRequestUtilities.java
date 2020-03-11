package com.ibm.nagios.hmc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.ibm.nagios.hmc.cert.InstallCertificate;

/**
 * This is a wrapper class for Java HTTP request and response operations,
 * mainly used by HMC commands to send REST requests and analyze the responses.
 *
 */
public class HTTPRequestUtilities {
    public static int rc = HttpsURLConnection.HTTP_OK;

    /**
     * this must be called after a request, otherwise, you always get 200
     *
     * @return
     */
    public static int getresponseCode() {
        int tempRc = rc;
        rc = HttpsURLConnection.HTTP_OK;
        return tempRc;
    }

    /**
     * Send request to server.
     *
     */
    private static synchronized InputStream sendRequest(String sURL, String method, String contentType, Map<String, String> header, String body) throws Exception {
        DataOutputStream wr = null;
        String oldTrustStorePath = null;

        try {
            //set the trusted store path temporary
            oldTrustStorePath = Utilities.setTrustedStorePath();

            URL url = new URL(sURL);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            //temp solution, should not be used in production environment
            con.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            con.setSSLSocketFactory(InstallCertificate.getSSLFactory());

            //set the correct properties
            con.setRequestMethod(method);
            //set connection timeout, the default is 0(infinite)
            con.setConnectTimeout(10 * 60 * 1000);
            if (contentType != null) {//for now, only GET requests do not have this property
                con.setRequestProperty("Content-Type", contentType);
                if (method.equals("POST")) {
                    con.addRequestProperty("Accept", "application/atom+xml");
                    con.addRequestProperty("Accept", "application/json"); //@cdllij have to set this since DS8K do NOT support application/atom+xml type
                }
            } else {
                con.addRequestProperty("Accept", "application/atom+xml");
                con.addRequestProperty("Accept", "application/json");
            }

            if (method.equals("PUT") || method.equals("POST") || method.equals("DELETE")) {//set to true only for PUT and POST requests that need a body
                con.setDoOutput(true);
            }

            if (header != null) {
                //Set properties in
                for (String key : header.keySet()) {
                    con.setRequestProperty(key, header.get(key));
                }
            }


            if (body != null) {//POST and PUT need a body
                wr = new DataOutputStream(con.getOutputStream());
                wr.write(body.getBytes());//changed to this, in order to fix the messy code issue when there are Chinese characters
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            rc = responseCode;//save it for future use
            if (responseCode < HttpsURLConnection.HTTP_OK
                    || responseCode >= HttpsURLConnection.HTTP_MULT_CHOICE) {
                //log the error response
                InputStream err_is = con.getErrorStream();
                if (err_is != null) {
                    BufferedReader err_br = new BufferedReader(new InputStreamReader(err_is));
                    String line = null;
                    StringBuffer sb = new StringBuffer();
                    while ((line = err_br.readLine()) != null) {
                        sb.append(line);
                        sb.append(" ");
                    }
                    throw new Exception(sb.toString());
                } else {
                    throw new Exception(HMCConstants.MESSAGE_REST_NOT_WORK);
                }
            }
            return con.getInputStream();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (wr != null) {
                    wr.close();
                    wr = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //rollback trusted store path
            Utilities.rollbackTrustedStorePath(oldTrustStorePath);
        }
    }

    /**
     * Send PUT request to HMC server.
     *
     * @param sURL        REST URL
     * @param contentType request contentType
     * @param header      request header
     * @param body        request boby
     * @return the output stream of the response
     * @throws Exception
     */
    public static InputStream sendPutRequest(String sURL, String contentType, String header, String body) throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        //The default header used here is X-API-Session
        if (header != null)
            headers.put(HMCConstants.XML_SESSION_ID, header);
        return sendRequest(sURL, "PUT", contentType, headers, body);
    }

    /**
     * Send GET request to HMC server.
     *
     * @param sURL        REST URL
     * @param contentType request contentType
     * @param header      request header
     * @return the output stream of the response
     * @throws Exception
     */
    public static InputStream sendGetRequest(String sURL, String contentType, String header) throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        //The default header used here is X-API-Session
        if (header != null)
            headers.put(HMCConstants.XML_SESSION_ID, header);
        return sendRequest(sURL, "GET", contentType, headers, null);
    }
}

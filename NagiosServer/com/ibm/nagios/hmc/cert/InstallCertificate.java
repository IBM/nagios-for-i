package com.ibm.nagios.hmc.cert;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.ibm.nagios.hmc.HMCConstants;
import com.ibm.nagios.hmc.HMCInfo;
import com.ibm.nagios.hmc.Utilities;

public class InstallCertificate {
    public static ConcurrentHashMap<String, String> preCheckStatus = new ConcurrentHashMap<String, String>();
    
    private static void copyFile(String source, String target) throws Exception {
        File s = new File(source);
        File t = new File(target);
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(s);
            out = new FileOutputStream(t);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            throw new Exception("copy certificate file failed: " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            //remove the source. bad idea?
            boolean removed = s.delete();
            if (removed) {
                System.out.println("Removal of 'jssecacerts' from " + s.getAbsolutePath() + " was successful");
            } else {
                System.out.println("Removal of 'jssecacerts' from " + s.getAbsolutePath() + " failed");
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
        }
    }

    /**
     * This method is used to check whether the certificate from HMC server is installed.
     *
     * @param parameters, the host name and port number of the server, such as hmc_host_name:12443
     * @param {@link      Arguments} argument, necessary information to complete this task, such as the keystore password and whether copying the key store file to system security directory.
     * @return true if certificate is already installed, otherwise, false.
     */
    public static boolean isCertificateInstalled(HMCInfo hmcInfo) {
        boolean installed = false;
        String system;
        String port;
        char[] password;

        system = hmcInfo.getSystem();
        port = hmcInfo.getPort() == null ? HMCConstants.HMC_PORT : hmcInfo.getPort();
        //get correct arguments
        String p = hmcInfo.getKsPassword() == null ? HMCConstants.DEFAULT_KEYSTORE_PASSWORD : hmcInfo.getKsPassword();
        password = p.toCharArray();

        //get keystore file
        char SEP = File.separatorChar;
        File dir = new File(System.getProperty("java.home") + SEP + "lib" + SEP + "security");
        File file = new File(dir, "jssecacerts");
        if (file.isFile() == false) {
            file = new File(dir, "cacerts");
        }

        //load the keystore file, default value is changeit
        KeyStore ks = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(in, password);

        } catch (Exception e) {
            e.printStackTrace();
            return installed;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.println("InstallCertificate.isCertificateInstalled() :: can not close the input stream of the created keystore file");
                }
                in = null;
            }
        }

        //SSL setup and hand shake
        CustomTrustManager tm = null;
        SSLSocket socket = null;
        try {
            SSLContext context = SSLContext.getInstance(HMCConstants.SSL_TLS_VERSION);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
            tm = new CustomTrustManager(defaultTrustManager);
            context.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory factory = context.getSocketFactory();
            socket = (SSLSocket) factory.createSocket(system, Integer.valueOf(port));
            socket.setSoTimeout(10000);
            socket.startHandshake();
            installed = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return installed;
    }

    private static class CustomTrustManager implements X509TrustManager {
        private final X509TrustManager tm;
        private X509Certificate[] chain;

        CustomTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {
            //throw new UnsupportedOperationException();
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }


    public static SSLSocketFactory getSSLFactory() throws Exception {
        SSLSocketFactory factory = null;
        try {
            SSLContext context = SSLContext.getInstance(HMCConstants.SSL_TLS_VERSION);
            context.init(null, null, null);
            factory = context.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return factory;
    }

    /**
     * This method is used to install the certificates from HMC server in order to take advantage of the REST service.
     *
     * @param parameters, the host name and port number of the server, such as hmc_host_name:12443
     * @param {@link      Arguments} argument, necessary information to complete this task, such as the keystore password and whether copying the key store file to system security directory.
     * @return return the {@link ResultCode} to indicate if it's successful.
     * @throws Exception
     */
    public static void install_all(HMCInfo hmcInfo, String ksPass) throws Exception {
        String host;
        int port;
        char[] password;

        host = hmcInfo.getSystem();
        port = Integer.valueOf(hmcInfo.getPort() == null ? HMCConstants.HMC_PORT : hmcInfo.getPort());
        //get correct arguments
        String p = hmcInfo.getKsPassword() == null ? HMCConstants.DEFAULT_KEYSTORE_PASSWORD : hmcInfo.getKsPassword();
        password = p.toCharArray();

        //ping
        boolean pingable = Utilities.pingPort(host, port, 5000);
        if (!pingable) {
            throw new Exception("Cannot connect to " + hmcInfo.getSystem());
        }

        //get keystore file 
        char SEP = File.separatorChar;
        //jvm path
        File dir = new File(System.getProperty("java.home") + SEP + "lib" + SEP + "security");

        File file = new File(dir, "jssecacerts");

        if (file.isFile() == false) {
            file = new File(dir, "cacerts");
        }

        KeyStore ks = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(in, password);
        } catch (Exception e) {
            throw new Exception("install certificate failed: " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
        }

        //SSL setup and hand shake
        CustomTrustManager tm = null;
        SSLSocket socket = null;
        try {
            SSLContext context = SSLContext.getInstance(HMCConstants.SSL_TLS_VERSION);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
            tm = new CustomTrustManager(defaultTrustManager);
            context.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory factory = context.getSocketFactory();
 
            socket = (SSLSocket) factory.createSocket(host, port);
            socket.setSoTimeout(10000);
            socket.startHandshake();
        } catch (Exception e) {
            e.printStackTrace();//Certificate has not been installed. This exception should be ignored.
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        X509Certificate[] chain = tm.chain;
        if (chain == null) {
            throw new Exception("Could not obtain server certificate chain");
        }
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            for (int i = 0; i < chain.length; i++) {
                X509Certificate cert = chain[i]; 
                sha1.update(cert.getEncoded());
                md5.update(cert.getEncoded()); 
            }
        } catch (Exception e) {
            throw new Exception("load server certificate chain failed: " + e.getMessage());
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream("jssecacerts");
            for (int i = chain.length - 1; i >= 0; i--) {
                X509Certificate cert = chain[i];//install all certificates
                String alias = host + "-" + (0 + i);

                ks.setCertificateEntry(alias, cert);
            }
            ks.store(out, password);
        } catch (Exception e) {
            throw new Exception("store server certificate chain failed: " + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String target = null;
        target = System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "jssecacerts";
        copyFile("jssecacerts", target);
    }

}

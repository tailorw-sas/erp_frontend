package com.kynsof.share.utils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SSLContextConfig {

    public static void sslContextConfig() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sslContext;
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                SSLContext.setDefault(sslContext);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(SSLContextConfig.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (KeyManagementException ex) {
            Logger.getLogger(SSLContextConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

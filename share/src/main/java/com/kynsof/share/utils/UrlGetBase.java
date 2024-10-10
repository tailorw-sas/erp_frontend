package com.kynsof.share.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlGetBase {
    public static String getBaseUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            // Construye el endpoint base usando el protocolo, host y puerto si es necesario
            String baseUrl = url.getProtocol() + "://" + url.getHost();
            if (url.getPort() != -1) {
                baseUrl += ":" + url.getPort();
            }
            return baseUrl + "/";
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}

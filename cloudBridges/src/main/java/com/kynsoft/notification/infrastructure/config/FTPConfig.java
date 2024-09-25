package com.kynsoft.notification.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FTPConfig {
    private String server;
    private int port;
    private String user;
    private String password;
    private int bufferSize;
    private int connectTimeout;
    private int soTimeout;

}

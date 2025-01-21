package com.tailorw.tcaInnsist.infrastructure.repository.config;

import lombok.Data;

@Data
public class DBConfigurationProperties {
    private String url;
    private String username;
    private String password;
}

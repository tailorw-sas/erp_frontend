package com.kynsoft.finamer.insis.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InnsistTcaConfigurationPropertiesDto {
    private UUID id;
    private String tradingCompany;
    private String hotel;
    private String roomType;
    private String server;
    private int port;
    private String dbName;
    private String userName;
    private String password;

}

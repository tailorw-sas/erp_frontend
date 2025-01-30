package com.tailorw.tcaInnsist.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageConnectionDto {
    private UUID id;
    private String server;
    private String port;
    private String dbName;
    private String userName;
    private String password;
}

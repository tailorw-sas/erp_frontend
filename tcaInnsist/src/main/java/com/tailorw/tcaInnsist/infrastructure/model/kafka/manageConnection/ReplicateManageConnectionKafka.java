package com.tailorw.tcaInnsist.infrastructure.model.kafka.manageConnection;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReplicateManageConnectionKafka {
    private UUID id;
    private String server;
    private String port;
    private String dbName;
    private String userName;
    private String password;
}

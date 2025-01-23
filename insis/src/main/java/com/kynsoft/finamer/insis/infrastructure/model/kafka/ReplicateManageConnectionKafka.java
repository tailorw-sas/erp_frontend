package com.kynsoft.finamer.insis.infrastructure.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReplicateManageConnectionKafka {
    private UUID id;
    private String server;
    private String port;
    private String dbName;
    private String userName;
    private String password;
}

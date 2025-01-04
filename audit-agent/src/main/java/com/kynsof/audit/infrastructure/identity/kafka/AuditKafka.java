package com.kynsof.audit.infrastructure.identity.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AuditKafka implements Serializable {

    private String auditRegisterId;

    private String entityName;

    private String username;

    private String action;

    private String data;

    private String tag;

    private String serviceName;

    private LocalDateTime time;

}

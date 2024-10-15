package com.kynsoft.finamer.audit.infrastructure.identity.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuditKafka implements Serializable {

    private String entityName;

    private String username;

    private String action;

    private String data;

    private String tag;

    private LocalDateTime time;

    private String serviceName;

    private String auditRegisterId;
}

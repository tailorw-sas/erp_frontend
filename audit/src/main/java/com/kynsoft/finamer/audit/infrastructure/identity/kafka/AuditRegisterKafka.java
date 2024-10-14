package com.kynsoft.finamer.audit.infrastructure.identity.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class AuditRegisterKafka implements Serializable {
    private String auditRegisterId;
    private String serviceName;

    public AuditRegisterKafka(String auditRegisterId, String serviceName) {
        this.auditRegisterId = auditRegisterId;
        this.serviceName = serviceName;
    }
}

package com.kynsof.audit.infrastructure.identity.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class AuditRegisterKafka implements Serializable {
    private String auditRegisterId;
    private String serviceName;
    private String entityName;
}

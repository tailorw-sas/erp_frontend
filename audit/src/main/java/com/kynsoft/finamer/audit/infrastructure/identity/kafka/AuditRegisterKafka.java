package com.kynsoft.finamer.audit.infrastructure.identity.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditRegisterKafka implements Serializable {
    private String auditRegisterId;
    private String serviceName;
    private String entityName;
}

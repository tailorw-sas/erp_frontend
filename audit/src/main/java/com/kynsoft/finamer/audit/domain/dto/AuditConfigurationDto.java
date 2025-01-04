package com.kynsoft.finamer.audit.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
@AllArgsConstructor
public class AuditConfigurationDto {
    private UUID id;
    private boolean auditCreate;
    private boolean auditUpdate;
    private boolean auditDelete;
    private String serviceName;
    private String entityName;

}

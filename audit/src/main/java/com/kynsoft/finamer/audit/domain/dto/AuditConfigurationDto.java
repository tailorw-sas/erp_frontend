package com.kynsoft.finamer.audit.domain.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.index.Indexed;

public record AuditConfigurationDto (String id,String entityName,boolean auditCreation,boolean auditUpdate,boolean auditDelete) {
}

package com.kynsof.audit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
public class AuditRegisterRecordDto {

    private String id;
    private String entityName;
    private boolean auditUpdate;
    private boolean auditCreate;
    private boolean auditDelete;
}

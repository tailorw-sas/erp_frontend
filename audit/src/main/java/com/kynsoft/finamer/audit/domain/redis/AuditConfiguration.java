package com.kynsoft.finamer.audit.domain.redis;

import com.kynsoft.finamer.audit.domain.dto.AuditConfigurationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash("audit_register")
public class AuditConfiguration {
    @Id
    private String id;
    @Indexed
    private String entityName;
    private boolean auditUpdate;
    private boolean auditCreate;
    private boolean auditDelete;

    public AuditConfigurationDto toAggregate(){
        return  new AuditConfigurationDto(id,entityName,auditCreate,auditUpdate,auditDelete);
    }
}
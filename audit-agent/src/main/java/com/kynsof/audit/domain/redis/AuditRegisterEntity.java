package com.kynsof.share.core.domain.audit.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash("audit_register")
public class AuditRegisterEntity {
    @Id
    private String id;
    @Indexed
    private String entityName;
    private boolean auditUpdate;
    private boolean auditCreate;
    private boolean auditDelete;

}

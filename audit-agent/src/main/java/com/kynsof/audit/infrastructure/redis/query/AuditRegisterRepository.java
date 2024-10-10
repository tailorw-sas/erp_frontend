package com.kynsof.audit.infrastructure.redis.query;

import com.kynsof.share.core.domain.audit.redis.AuditRegisterEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditRegisterRepository extends CrudRepository<AuditRegisterEntity,String> {

    Optional<AuditRegisterEntity> findAuditRegisterEntityByServiceName(String serviceName);
}

package com.kynsoft.finamer.audit.infrastructure.repository;

import com.kynsoft.finamer.audit.domain.redis.AuditConfiguration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRedisRepository extends CrudRepository<AuditConfiguration,String>,
        PagingAndSortingRepository<AuditConfiguration,String> {
}

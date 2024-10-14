package com.kynsoft.finamer.audit.infrastructure.repository;

import com.kynsoft.finamer.audit.infrastructure.identity.jpa.AuditConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuditConfigurationRepository extends JpaRepository<AuditConfiguration,String>,
        PagingAndSortingRepository<AuditConfiguration,String> {

    Optional<AuditConfiguration> findAuditConfigurationByServiceNameAndId(String serviceName, UUID registerId);
}

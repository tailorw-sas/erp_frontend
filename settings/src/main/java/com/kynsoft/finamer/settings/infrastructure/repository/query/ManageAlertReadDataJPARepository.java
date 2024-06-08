package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageAlerts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ManageAlertReadDataJPARepository extends JpaRepository<ManageAlerts, UUID> {
    Page<ManageAlerts> findAll(Specification specification, Pageable pageable);
    @Query("SELECT COUNT(b) FROM ManageAlerts b WHERE b.code = :code")
    Long countByCode(@Param("code") String code);
}

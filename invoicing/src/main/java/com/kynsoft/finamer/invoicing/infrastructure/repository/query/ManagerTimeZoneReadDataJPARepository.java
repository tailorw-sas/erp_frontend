package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManagerTimeZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManagerTimeZoneReadDataJPARepository extends JpaRepository<ManagerTimeZone, UUID>,
        JpaSpecificationExecutor<ManagerTimeZone> {

    Page<ManagerTimeZone> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManagerTimeZone b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

}

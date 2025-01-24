package com.tailorw.finamer.scheduler.infrastructure.repository.query;

import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcess;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BusinessProcessReadDataJPARepository extends JpaRepository<BusinessProcess, UUID>, JpaSpecificationExecutor<BusinessProcess> {

    Optional<BusinessProcess> findByCode(String code);

    Optional<BusinessProcess> findByCodeAndStatus(String code, String status);

    @Query("select COUNT(s) FROM BusinessProcess bp JOIN bp.schedulers s WHERE bp.id = :id AND s.status IN ('ACTIVE', 'INACTIVE')")
    long countActiveAndInactiveBusinessProcessSchedulers(UUID id);

    Page<BusinessProcess> findAll(Specification specification, Pageable pageable);
}

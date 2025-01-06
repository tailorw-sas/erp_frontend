package com.tailorw.finamer.scheduler.infrastructure.repository.query;

import com.tailorw.finamer.scheduler.infrastructure.model.ExecutionDateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ExecutionDateTypeReadDataJPARepository extends JpaRepository<ExecutionDateType, UUID>, JpaSpecificationExecutor<ExecutionDateType> {

    Optional<ExecutionDateType> findByCodeAndDeletedAtIsNull(String code);

    @Query("select s FROM ExecutionDateType s WHERE s.code = :code and s.status IN ('ACTIVE', 'INACTIVE')")
    Optional<ExecutionDateType> findByCode(String code);

    Page<ExecutionDateType> findAll(Specification specification, Pageable pageable);
}

package com.tailorw.finamer.scheduler.infrastructure.repository.query;

import com.tailorw.finamer.scheduler.infrastructure.model.ProcessingDateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ProcessingDateTypeReadDataJPARepository extends JpaRepository<ProcessingDateType, UUID>, JpaSpecificationExecutor<ProcessingDateType> {

    Optional<ProcessingDateType> findByCodeAndDeletedAtIsNull(String code);

    @Query("select s FROM ProcessingDateType s WHERE s.code = :code and s.status IN ('ACTIVE', 'INACTIVE')")
    Optional<ProcessingDateType> findByCode(String code);

    Page<ProcessingDateType> findAll(Specification specification, Pageable pageable);
}

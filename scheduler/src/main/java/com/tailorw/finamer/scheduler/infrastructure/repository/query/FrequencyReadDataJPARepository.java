package com.tailorw.finamer.scheduler.infrastructure.repository.query;

import com.tailorw.finamer.scheduler.infrastructure.model.Frequency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface FrequencyReadDataJPARepository extends JpaRepository<Frequency, UUID>, JpaSpecificationExecutor<Frequency> {

    Optional<Frequency> findByCodeAndDeletedAtIsNull(String code);

    @Query("select s FROM Frequency s WHERE s.code = :code and s.status IN ('ACTIVE', 'INACTIVE')")
    Optional<Frequency> findByCode(String code);

    Page<Frequency> findAll(Specification specification, Pageable pageable);
}

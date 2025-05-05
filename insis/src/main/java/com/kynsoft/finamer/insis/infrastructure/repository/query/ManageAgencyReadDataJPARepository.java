package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.ManageAgency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ManageAgencyReadDataJPARepository extends JpaRepository<ManageAgency, UUID>, JpaSpecificationExecutor<ManageAgency> {

    Optional<ManageAgency> findByCode(String code);

    Page<ManageAgency> findAll(Specification specification, Pageable pageable);

    @Query("SELECT m.code, m.id FROM ManageAgency m WHERE m.code IN :codes")
    List<Object[]> findAgencyIdsByCodes(@Param("codes") List<String> codes);

    List<ManageAgency> findByIdIn(List<UUID> ids);

    List<ManageAgency> findByCodeIn(List<String> codes);
}

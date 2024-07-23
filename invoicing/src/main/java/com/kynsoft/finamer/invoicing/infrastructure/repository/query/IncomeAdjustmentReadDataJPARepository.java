package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.IncomeAdjustment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface IncomeAdjustmentReadDataJPARepository extends JpaRepository<IncomeAdjustment, UUID>,
        JpaSpecificationExecutor<IncomeAdjustment> {

    Page<IncomeAdjustment> findAll(Specification specification, Pageable pageable);
}

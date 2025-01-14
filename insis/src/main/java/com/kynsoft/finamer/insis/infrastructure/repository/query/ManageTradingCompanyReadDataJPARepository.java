package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.ManageTradingCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ManageTradingCompanyReadDataJPARepository extends JpaRepository<ManageTradingCompany, UUID>, JpaSpecificationExecutor<ManageTradingCompany> {

    Page<ManageTradingCompany> findAll(Specification specification, Pageable pageable);
}

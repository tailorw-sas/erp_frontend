package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.ManageB2BPartnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ManageB2BPartnerTypeReadDataJPARepository extends JpaRepository<ManageB2BPartnerType, UUID>, JpaSpecificationExecutor<ManageB2BPartnerType> {
    Page<ManageB2BPartnerType> findAll(Specification specification, Pageable pageable);
}

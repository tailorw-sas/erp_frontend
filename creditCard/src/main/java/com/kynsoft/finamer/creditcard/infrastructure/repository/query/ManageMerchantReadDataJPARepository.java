package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageMerchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ManageMerchantReadDataJPARepository extends JpaRepository<ManageMerchant, UUID>,
        JpaSpecificationExecutor<ManageMerchant> {
    Page<ManageMerchant> findAll(Specification specification, Pageable pageable);

}

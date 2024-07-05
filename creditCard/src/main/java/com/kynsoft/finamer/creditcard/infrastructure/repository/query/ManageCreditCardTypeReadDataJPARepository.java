package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageCreditCardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ManageCreditCardTypeReadDataJPARepository extends JpaRepository<ManageCreditCardType, UUID>,
        JpaSpecificationExecutor<ManageCreditCardType> {

    Page<ManageCreditCardType> findAll(Specification specification, Pageable pageable);

}

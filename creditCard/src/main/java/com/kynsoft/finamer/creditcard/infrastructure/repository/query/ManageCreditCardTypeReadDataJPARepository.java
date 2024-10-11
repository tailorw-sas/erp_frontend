package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageCreditCardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageCreditCardTypeReadDataJPARepository extends JpaRepository<ManageCreditCardType, UUID>,
        JpaSpecificationExecutor<ManageCreditCardType> {

    Page<ManageCreditCardType> findAll(Specification specification, Pageable pageable);
    Optional<ManageCreditCardType> findByFirstDigit(Integer digit);

}

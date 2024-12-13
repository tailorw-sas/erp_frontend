package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManagePaymentTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ManagePaymentTransactionTypeReadDataJPARepository extends JpaRepository<ManagePaymentTransactionType, UUID>,
        JpaSpecificationExecutor<ManagePaymentTransactionType> {

    Page<ManagePaymentTransactionType> findAll(Specification specification, Pageable pageable);

    Optional<ManagePaymentTransactionType> findByCode(String code);

    @Query("SELECT b FROM ManagePaymentTransactionType b WHERE b.defaults = true AND b.status = 'ACTIVE'")
    Optional<ManagePaymentTransactionType> findByDefaults();
}

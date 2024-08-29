package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ManagePaymentSourceReadDataJPARepository extends JpaRepository<ManagePaymentSource, UUID>,
        JpaSpecificationExecutor<ManagePaymentSource> {

    Page<ManagePaymentSource> findAll(Specification specification, Pageable pageable);
    Optional<ManagePaymentSource> findByCodeAndStatus(String code, String status);

    @Query("SELECT b FROM ManagePaymentSource b WHERE b.expense = true")
    Optional<ManagePaymentSource> findByExpense();
}

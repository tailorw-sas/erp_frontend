package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface ManagePaymentStatusReadDataJpaRepository extends JpaRepository<ManagePaymentStatus, UUID> {
    Page<ManagePaymentStatus> findAll(Specification specification, Pageable pageable);

    Optional<ManagePaymentStatus> findByCodeAndStatus(String code, String Status);

    @Query("SELECT b FROM ManagePaymentStatus b WHERE b.applied = true")
    Optional<ManagePaymentStatus> findByApplied();
}

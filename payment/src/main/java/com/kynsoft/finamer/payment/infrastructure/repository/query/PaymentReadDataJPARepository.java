package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentReadDataJPARepository extends JpaRepository<Payment, UUID>,
        JpaSpecificationExecutor<Payment> {

    Page<Payment> findAll(Specification specification, Pageable pageable);
    boolean existsPaymentByPaymentId(long paymentId);

    Optional<Payment> findPaymentByPaymentId(long paymentId);
}

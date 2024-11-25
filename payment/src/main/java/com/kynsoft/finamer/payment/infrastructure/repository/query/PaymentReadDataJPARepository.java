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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PaymentReadDataJPARepository extends JpaRepository<Payment, UUID>,
        JpaSpecificationExecutor<Payment> {

    Page<Payment> findAll(Specification specification, Pageable pageable);
    boolean existsPaymentByPaymentId(long paymentId);

    Optional<Payment> findPaymentByPaymentId(long paymentId);

    //@Query("SELECT COUNT(b) FROM Payment b WHERE b.agency.id = :agencyId AND paymentBalance > 0 OR depositBalance > 0")
    @Query("SELECT COUNT(p) FROM Payment p LEFT JOIN p.agency a WHERE a.id = :agencyId AND (p.paymentBalance > 0 OR p.depositBalance > 0)")
    Long countByAgency(@Param("agencyId") UUID agencyId);

    @Query("SELECT COUNT(b) FROM Payment b WHERE b.agency.id = :agencyId")
    Long countByAgencyOther(@Param("agencyId") UUID agencyId);

    @Query("SELECT COALESCE(MAX(p.paymentId), 0) FROM Payment p")
    Long findMaxId();
}

package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjection;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjectionSimple;
import com.kynsoft.finamer.payment.infrastructure.identity.Payment;
import com.kynsoft.finamer.payment.infrastructure.repository.query.payments.PaymentCustomRepository;
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
        JpaSpecificationExecutor<Payment>, PaymentCustomRepository {

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

    @Query("SELECT new com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjection("
            + "p.id, p.paymentId, p.paymentAmount, p.paymentBalance, p.depositAmount, p.depositBalance, p.otherDeductions, "
            + "p.identified, p.notIdentified, p.notApplied, p.applied) "
            + "FROM Payment p "
            + "WHERE p.paymentId = :paymentId")
    Optional<PaymentProjection> findPaymentId(@Param("paymentId") long paymentId);

    @Query("SELECT new com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjectionSimple("
            + "p.id, p.paymentId) "
            + "FROM Payment p "
            + "WHERE p.paymentId = :paymentId")
    Optional<PaymentProjectionSimple> findPaymentIdCacheable(@Param("paymentId") long paymentId);

    @Query("SELECT new com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjection("
            + "p.id, p.paymentId, p.paymentAmount, p.paymentBalance, p.depositAmount, p.depositBalance, p.otherDeductions, "
            + "p.identified, p.notIdentified, p.notApplied, p.applied) "
            + "FROM Payment p "
            + "WHERE p.id = :id")
    Optional<PaymentProjection> getPaymentByIdProjection(@Param("id") UUID id);
}

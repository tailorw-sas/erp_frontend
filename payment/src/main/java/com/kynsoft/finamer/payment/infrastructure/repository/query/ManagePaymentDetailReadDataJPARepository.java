package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.domain.dto.PaymentDetailSimpleDto;
import com.kynsoft.finamer.payment.domain.dto.projection.paymentDetails.PaymentDetailSimple;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentDetail;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManagePaymentDetailReadDataJPARepository extends JpaRepository<PaymentDetail, UUID>,
        JpaSpecificationExecutor<PaymentDetail> {

    Page<PaymentDetail> findAll(Specification specification, Pageable pageable);

    Optional<PaymentDetail> findByPaymentDetailId(int id);

    boolean existsByPaymentDetailId(int id);

    List<PaymentDetail> findByIdIn(List<UUID> ids);

    @Query("Select pd from PaymentDetail pd where pd.payment.id=:paymentId")
    Optional<List<PaymentDetail>> findAllByPayment(@Param("paymentId") UUID paymentId);

    @Query("Select pd from PaymentDetail pd where pd.paymentDetailId = :paymentDetailId")
    Optional<PaymentDetail> findByPaymentDetailId(@Param("paymentDetailId") Long paymentDetailId);

    @Query("SELECT COUNT(pd) FROM PaymentDetail pd WHERE pd.payment.id = :payment AND pd.transactionType.deposit = true AND pd.canceledTransaction = false")
    Long countByPaymentDetailIdAndTransactionTypeDeposit(@Param("payment") UUID payment);

    @Query("SELECT COUNT(pd) FROM PaymentDetail pd where pd.payment.id = :paymentId AND pd.applayPayment = true")
    Long countByApplyPaymentAndPaymentId(@Param("id") UUID paymentId);

    @Query("SELECT new com.kynsoft.finamer.payment.domain.dto.PaymentDetailSimpleDto(" +
            "pd.id, pd.applyDepositValue, tt.deposit, pp.id) " +
            "FROM PaymentDetail pd " +
            "JOIN pd.transactionType tt " +
            "LEFT JOIN pd.payment pp " +
            "WHERE pd.paymentDetailId = :id")
    Optional<PaymentDetailSimpleDto> findSimpleDetailByGenId(@Param("id") int id);

    @Query("SELECT new com.kynsoft.finamer.payment.domain.dto.projection.paymentDetails.PaymentDetailSimple(" +
            "pd.id, pd.paymentDetailId) " +
            "FROM PaymentDetail pd " +
            "WHERE pd.paymentDetailId = :id")
    Optional<PaymentDetailSimple> findPaymentDetailsSimpleCacheableByGenId(@Param("id") int id);
}

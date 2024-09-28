package com.kynsoft.finamer.payment.infrastructure.repository.query;

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

    @Query("Select pd from PaymentDetail pd where pd.payment.id=:paymentId")
    Optional<List<PaymentDetail>> findAllByPayment(@Param("paymentId") UUID paymentId);
}

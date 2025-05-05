package com.kynsoft.finamer.payment.infrastructure.repository.query.payments;

import com.kynsoft.finamer.payment.infrastructure.identity.Payment;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentDetail;
import com.kynsoft.finamer.payment.infrastructure.identity.projection.PaymentSearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentDetailCustomRepository {

    Optional<PaymentDetail> findByIdCustom(UUID id);

    Optional<PaymentDetail> findByPaymentDetailIdCustom(int id);

    List<PaymentDetail> findAllByPaymentIdCustom(UUID id);

    List<PaymentDetail> findChildrensByParentId(Long parentId);

    List<PaymentDetail> findAllByPaymentGenIdIn(List<Long> genIds);

    Page<PaymentDetail> findAllCustom(Specification<PaymentDetail> specification, Pageable pageable);
}

package com.kynsoft.finamer.payment.infrastructure.repository.query.payments;

import com.kynsoft.finamer.payment.infrastructure.identity.PaymentDetail;

import java.util.List;
import java.util.UUID;

public interface PaymentDetailCustomRepository {

    List<PaymentDetail> findAllByPaymentId(UUID id);
}

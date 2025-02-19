package com.kynsoft.finamer.payment.infrastructure.services;


import com.kynsoft.finamer.payment.infrastructure.identity.PaymentView;
import com.kynsoft.finamer.payment.infrastructure.repository.query.view.PaymentViewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentViewService {

    private final PaymentViewRepository paymentViewRepository;

    public PaymentViewService(PaymentViewRepository paymentViewRepository) {
        this.paymentViewRepository = paymentViewRepository;
    }

    public List<PaymentView> getAllPayments() {
        return paymentViewRepository.findAll();
    }
}
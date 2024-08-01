package com.kynsoft.finamer.payment.infrastructure.excel.event;

import com.kynsoft.finamer.payment.domain.excel.PaymentImportError;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportErrorRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentImportErrorHandler implements ApplicationListener<PaymentImportErrorEvent> {
    private final PaymentImportErrorRepository paymentImportErrorRepository;

    public PaymentImportErrorHandler(PaymentImportErrorRepository paymentImportErrorRepository) {
        this.paymentImportErrorRepository = paymentImportErrorRepository;
    }

    @Override
    public void onApplicationEvent(PaymentImportErrorEvent event) {
        PaymentImportError paymentImportError = (PaymentImportError) event.getSource();
        paymentImportErrorRepository.save(paymentImportError);
    }
}

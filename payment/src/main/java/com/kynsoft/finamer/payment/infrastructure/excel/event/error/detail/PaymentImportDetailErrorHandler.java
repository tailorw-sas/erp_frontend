package com.kynsoft.finamer.payment.infrastructure.excel.event.error.detail;

import com.kynsoft.finamer.payment.domain.excel.error.PaymentBankRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportBankErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportDetailErrorRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentImportDetailErrorHandler implements ApplicationListener<PaymentImportDetailErrorEvent> {
    private final PaymentImportDetailErrorRepository paymentImportErrorRepository;

    public PaymentImportDetailErrorHandler(PaymentImportDetailErrorRepository paymentImportErrorRepository) {
        this.paymentImportErrorRepository = paymentImportErrorRepository;
    }


    @Override
    public void onApplicationEvent(PaymentImportDetailErrorEvent event) {
        PaymentDetailRowError paymentImportError = (PaymentDetailRowError) event.getSource();
        paymentImportErrorRepository.save(paymentImportError);
    }
}

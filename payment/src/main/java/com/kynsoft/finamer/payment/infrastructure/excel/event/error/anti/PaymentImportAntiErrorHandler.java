package com.kynsoft.finamer.payment.infrastructure.excel.event.error.anti;

import com.kynsoft.finamer.payment.domain.excel.IPaymentImportExtrasFieldProcessor;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentAntiRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentBankRowError;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportAntiErrorRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentImportAntiErrorHandler implements ApplicationListener<PaymentImportAntiErrorEvent> {
    private final PaymentImportAntiErrorRepository antiErrorRepository;
    private final IPaymentImportExtrasFieldProcessor<PaymentAntiRowError> processor;

    public PaymentImportAntiErrorHandler(PaymentImportAntiErrorRepository antiErrorRepository,
                                         IPaymentImportExtrasFieldProcessor<PaymentAntiRowError> processor) {
        this.antiErrorRepository = antiErrorRepository;
        this.processor = processor;
    }

    @Override
    public void onApplicationEvent(PaymentImportAntiErrorEvent event) {
        PaymentAntiRowError paymentImportError = (PaymentAntiRowError) event.getSource();
        paymentImportError =processor.addExtrasField(paymentImportError);
        antiErrorRepository.save(paymentImportError);

    }
}

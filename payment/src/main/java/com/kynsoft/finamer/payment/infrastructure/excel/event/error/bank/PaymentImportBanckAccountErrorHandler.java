package com.kynsoft.finamer.payment.infrastructure.excel.event.error.bank;

import com.kynsoft.finamer.payment.domain.excel.error.PaymentBankRowError;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportBankErrorRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentImportBanckAccountErrorHandler implements ApplicationListener<PaymentImportBankAccountErrorEvent> {
    private final PaymentImportBankErrorRepository paymentImportErrorRepository;

    public PaymentImportBanckAccountErrorHandler(PaymentImportBankErrorRepository paymentImportErrorRepository) {
        this.paymentImportErrorRepository = paymentImportErrorRepository;
    }

    @Override
    public void onApplicationEvent(PaymentImportBankAccountErrorEvent event) {
        PaymentBankRowError paymentImportError = (PaymentBankRowError) event.getSource();
        paymentImportErrorRepository.save(paymentImportError);
    }
}

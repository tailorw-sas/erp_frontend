package com.kynsoft.finamer.payment.infrastructure.excel.event.error.expense;

import com.kynsoft.finamer.payment.domain.excel.error.PaymentBankRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseRowError;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportBankErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportExpenseErrorRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentImportExpenseErrorHandler implements ApplicationListener<PaymentImportExpenseErrorEvent> {
    private final PaymentImportExpenseErrorRepository paymentImportErrorRepository;

    public PaymentImportExpenseErrorHandler(PaymentImportExpenseErrorRepository paymentImportErrorRepository) {
        this.paymentImportErrorRepository = paymentImportErrorRepository;
    }


    @Override
    public void onApplicationEvent(PaymentImportExpenseErrorEvent event) {
        PaymentExpenseRowError paymentImportError = (PaymentExpenseRowError) event.getSource();
        paymentImportErrorRepository.save(paymentImportError);
    }
}

package com.kynsoft.finamer.payment.infrastructure.excel.event.error.expenseToBooking;

import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseBookingRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseRowError;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportExpenseBookingErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportExpenseErrorRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentImportExpenseToBookingErrorHandler implements ApplicationListener<PaymentImportExpenseToBookingErrorEvent> {
    private final PaymentImportExpenseBookingErrorRepository paymentImportErrorRepository;

    public PaymentImportExpenseToBookingErrorHandler(PaymentImportExpenseBookingErrorRepository paymentImportErrorRepository) {
        this.paymentImportErrorRepository = paymentImportErrorRepository;
    }


    @Override
    public void onApplicationEvent(PaymentImportExpenseToBookingErrorEvent event) {
        PaymentExpenseBookingRowError paymentImportError = (PaymentExpenseBookingRowError) event.getSource();
        paymentImportErrorRepository.save(paymentImportError);
    }
}

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
    private final PaymentExpenseBookingExtrasFieldProcessor extrasFieldProcessor;

    public PaymentImportExpenseToBookingErrorHandler(PaymentImportExpenseBookingErrorRepository paymentImportErrorRepository,
                                                     PaymentExpenseBookingExtrasFieldProcessor extrasFieldProcessor) {
        this.paymentImportErrorRepository = paymentImportErrorRepository;
        this.extrasFieldProcessor = extrasFieldProcessor;
    }


    @Override
    public void onApplicationEvent(PaymentImportExpenseToBookingErrorEvent event) {
        PaymentExpenseBookingRowError paymentImportError = (PaymentExpenseBookingRowError) event.getSource();
        extrasFieldProcessor.addExtrasField(paymentImportError);
        paymentImportErrorRepository.save(paymentImportError);
    }
}

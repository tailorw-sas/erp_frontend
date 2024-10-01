package com.kynsoft.finamer.payment.infrastructure.excel.event.error.expenseToBooking;

import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseBookingRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseRowError;
import org.springframework.context.ApplicationEvent;

public class PaymentImportExpenseToBookingErrorEvent extends ApplicationEvent {
    public PaymentImportExpenseToBookingErrorEvent(PaymentExpenseBookingRowError source) {
        super(source);
    }
}

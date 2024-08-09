package com.kynsoft.finamer.payment.infrastructure.excel.event.error.expense;

import com.kynsoft.finamer.payment.domain.excel.error.PaymentBankRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseRowError;
import org.springframework.context.ApplicationEvent;

public class PaymentImportExpenseErrorEvent extends ApplicationEvent {
    public PaymentImportExpenseErrorEvent(PaymentExpenseRowError source) {
        super(source);
    }
}

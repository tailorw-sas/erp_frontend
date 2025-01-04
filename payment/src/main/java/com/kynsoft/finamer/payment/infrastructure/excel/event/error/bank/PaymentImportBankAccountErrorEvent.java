package com.kynsoft.finamer.payment.infrastructure.excel.event.error.bank;

import com.kynsoft.finamer.payment.domain.excel.error.PaymentBankRowError;
import org.springframework.context.ApplicationEvent;

public class PaymentImportBankAccountErrorEvent extends ApplicationEvent {
    public PaymentImportBankAccountErrorEvent(PaymentBankRowError source) {
        super(source);
    }
}

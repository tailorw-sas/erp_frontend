package com.kynsoft.finamer.payment.infrastructure.excel.event.error.anti;

import com.kynsoft.finamer.payment.domain.excel.error.PaymentAntiRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentBankRowError;
import org.springframework.context.ApplicationEvent;

public class PaymentImportAntiErrorEvent extends ApplicationEvent {
    public PaymentImportAntiErrorEvent(PaymentAntiRowError source) {
        super(source);
    }
}

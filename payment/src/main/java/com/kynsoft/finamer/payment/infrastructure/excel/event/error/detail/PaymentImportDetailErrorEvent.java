package com.kynsoft.finamer.payment.infrastructure.excel.event.error.detail;

import com.kynsoft.finamer.payment.domain.excel.error.PaymentBankRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import org.springframework.context.ApplicationEvent;

public class PaymentImportDetailErrorEvent extends ApplicationEvent {
    public PaymentImportDetailErrorEvent(PaymentDetailRowError source) {
        super(source);
    }
}

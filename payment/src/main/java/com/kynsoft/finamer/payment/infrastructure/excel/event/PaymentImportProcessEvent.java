package com.kynsoft.finamer.payment.infrastructure.excel.event;

import org.springframework.context.ApplicationEvent;

public class PaymentImportProcessEvent extends ApplicationEvent {
    public PaymentImportProcessEvent(Object source) {
        super(source);
    }
}

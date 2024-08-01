package com.kynsoft.finamer.payment.infrastructure.excel.event;

import org.springframework.context.ApplicationEvent;

public class PaymentImportErrorEvent extends ApplicationEvent {
    public PaymentImportErrorEvent(Object source) {
        super(source);
    }
}

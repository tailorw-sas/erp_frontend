package com.kynsoft.finamer.payment.infrastructure.excel.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentImportErrorEvent extends ApplicationEvent {

    private final Object error;
    public PaymentImportErrorEvent(Object source, Object error) {
        super(source);
        this.error = error;
    }
}

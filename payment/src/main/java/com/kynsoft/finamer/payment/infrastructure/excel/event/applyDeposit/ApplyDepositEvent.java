package com.kynsoft.finamer.payment.infrastructure.excel.event.applyDeposit;

import org.springframework.context.ApplicationEvent;

public class ApplyDepositEvent extends ApplicationEvent {
    public ApplyDepositEvent(Object source) {
        super(source);
    }
}

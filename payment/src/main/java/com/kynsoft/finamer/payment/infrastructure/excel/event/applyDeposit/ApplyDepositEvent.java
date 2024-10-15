package com.kynsoft.finamer.payment.infrastructure.excel.event.applyDeposit;

import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class ApplyDepositEvent extends ApplicationEvent {
    public final boolean  fromImport;
    public ApplyDepositEvent(Object source, boolean fromImport) {
        super(source);
        this.fromImport = fromImport;
    }
}

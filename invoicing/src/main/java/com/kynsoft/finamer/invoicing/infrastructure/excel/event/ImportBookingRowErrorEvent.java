package com.kynsoft.finamer.invoicing.infrastructure.excel.event;

import org.springframework.context.ApplicationEvent;


public class ImportBookingRowErrorEvent extends ApplicationEvent {
    public ImportBookingRowErrorEvent(Object source) {
        super(source);
    }
}

package com.kynsoft.finamer.invoicing.infrastructure.excel.event.error;

import org.springframework.context.ApplicationEvent;


public class ImportBookingRowErrorEvent extends ApplicationEvent {
    public ImportBookingRowErrorEvent(Object source) {
        super(source);
    }
}

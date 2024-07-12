package com.kynsoft.finamer.invoicing.infrastructure.excel.event;

import org.springframework.context.ApplicationEvent;

public class ImportBookingProcessEvent extends ApplicationEvent {
    public ImportBookingProcessEvent(Object source) {
        super(source);
    }
}

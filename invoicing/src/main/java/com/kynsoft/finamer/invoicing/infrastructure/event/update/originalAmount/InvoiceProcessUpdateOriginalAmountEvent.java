package com.kynsoft.finamer.invoicing.infrastructure.event.update.originalAmount;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InvoiceProcessUpdateOriginalAmountEvent extends ApplicationEvent {

    private final UUID invoiceId;

    public InvoiceProcessUpdateOriginalAmountEvent(Object source, UUID invoiceId) {
        super(source);
        this.invoiceId = invoiceId;
    }
}

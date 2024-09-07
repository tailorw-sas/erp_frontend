package com.kynsoft.finamer.invoicing.domain.event.importStatus;

import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileImportProcessStatusDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateImportStatusEvent extends ApplicationEvent {

    private final InvoiceReconcileImportProcessStatusDto processStatusDto;

    public CreateImportStatusEvent(Object source, InvoiceReconcileImportProcessStatusDto processStatusDto) {
        super(source);
        this.processStatusDto = processStatusDto;
    }

}

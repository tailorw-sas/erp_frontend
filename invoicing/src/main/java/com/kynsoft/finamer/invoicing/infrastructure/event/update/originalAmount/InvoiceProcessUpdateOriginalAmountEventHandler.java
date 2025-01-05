package com.kynsoft.finamer.invoicing.infrastructure.event.update.originalAmount;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class InvoiceProcessUpdateOriginalAmountEventHandler implements ApplicationListener<InvoiceProcessUpdateOriginalAmountEvent> {

    private final IManageInvoiceService service;

    public InvoiceProcessUpdateOriginalAmountEventHandler(IManageInvoiceService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(InvoiceProcessUpdateOriginalAmountEvent event) {
        ManageInvoiceDto dto = this.service.findById(event.getInvoiceId());
        dto.setOriginalAmount(dto.getInvoiceAmount());
        this.service.update(dto);
    }
}

package com.kynsoft.finamer.invoicing.application.command.invoiceStatusHistory.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceStatusHistoryDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceStatusHistoryService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateInvoiceStatusHistoryCommandHandler implements ICommandHandler<CreateInvoiceStatusHistoryCommand> {

    private final IManageInvoiceService invoiceService;
    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;

    public CreateInvoiceStatusHistoryCommandHandler(IManageInvoiceService invoiceService, IInvoiceStatusHistoryService invoiceStatusHistoryService) {
        this.invoiceService = invoiceService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
    }

    @Override
    public void handle(CreateInvoiceStatusHistoryCommand command) {

        ManageInvoiceDto invoiceDto = invoiceService.findById(command.getInvoice());

        InvoiceStatusHistoryDto dto = new InvoiceStatusHistoryDto();

        dto.setId(UUID.randomUUID());
        dto.setInvoice(invoiceDto);
        dto.setDescription("The invoice data was inserted.");
        dto.setEmployee(command.getEmployee());
        dto.setInvoiceStatus(invoiceDto.getStatus());
        this.invoiceStatusHistoryService.create(dto);


    }
}

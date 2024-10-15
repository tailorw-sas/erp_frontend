package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateInvoiceCommand implements ICommand {

    private UUID id;
    private LocalDateTime invoiceDate;
    private UUID agency;
    private String employee;
    private EInvoiceStatus status;

    public UpdateInvoiceCommand(UUID id,  LocalDateTime invoiceDate, UUID agency, String employee, EInvoiceStatus status) {
        this.id = id;
        this.invoiceDate = invoiceDate;
        this.agency = agency;
        this.employee = employee;
        this.status = status;
    }

    public static UpdateInvoiceCommand fromRequest(UpdateInvoiceRequest request, UUID id) {
        return new UpdateInvoiceCommand(
                id, 
                request.getInvoiceDate(), request.getAgency(), request.getEmployee(), request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateInvoiceMessage(id);
    }
}

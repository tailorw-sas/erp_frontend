package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateInvoiceCommand implements ICommand {

    private UUID id;
    private LocalDateTime invoiceDate;
    private Boolean isManual;
    private Double invoiceAmount;
    private UUID hotel;
    private UUID agency;
    private UUID invoiceType;

    public UpdateInvoiceCommand(UUID id,  LocalDateTime invoiceDate, Boolean isManual, Double invoiceAmount, UUID hotel, UUID agency, UUID invoiceType, Status status) {
        this.id = id;
        this.invoiceDate = invoiceDate;
        this.isManual = isManual;
        this.invoiceAmount = invoiceAmount;
        this.hotel = hotel;
        this.agency = agency;
        this.invoiceType = invoiceType;
    }

    public static UpdateInvoiceCommand fromRequest(UpdateInvoiceRequest request, UUID id) {
        return new UpdateInvoiceCommand(
                id, request.getInvoiceDate(), request.getIsManual(), request.getInvoiceAmount(), request.getHotel(), request.getAgency(), request.getInvoiceType(), request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateInvoiceMessage(id);
    }
}

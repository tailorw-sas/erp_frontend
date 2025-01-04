package com.kynsoft.finamer.invoicing.application.command.manageInvoice.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateInvoiceCommand implements ICommand {

    private UUID id;
    private LocalDateTime invoiceDate;
    private LocalDate dueDate;
    private Boolean isManual;
    private Double invoiceAmount;
    private UUID hotel;
    private UUID agency;
    private EInvoiceType invoiceType;
    private Long invoiceId;

    public CreateInvoiceCommand(LocalDateTime invoiceDate, LocalDate dueDate, Boolean isManual,
            Double invoiceAmount, UUID hotel,
            UUID agency, EInvoiceType invoiceType, UUID id) {
        this.id = id;
        this.invoiceDate = invoiceDate;
        this.isManual = isManual;
        this.invoiceAmount = invoiceAmount;
        this.hotel = hotel;
        this.agency = agency;
        this.invoiceType = invoiceType;
        this.dueDate = dueDate;

    }

    public static CreateInvoiceCommand fromRequest(CreateInvoiceRequest request) {
        return new CreateInvoiceCommand(
                request.getInvoiceDate(), request.getDueDate(), request.getIsManual(), request.getInvoiceAmount(),
                request.getHotel(),
                request.getAgency(), request.getInvoiceType(),
                request.getId() != null ? request.getId() : UUID.randomUUID());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateInvoiceMessage(id, invoiceId);
    }
}

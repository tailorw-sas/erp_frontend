package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateInvoiceCommand implements ICommand {

    private UUID id;
    private LocalDateTime invoiceDate;
    private LocalDate reSendDate;
    private LocalDate dueDate;
    private Boolean isManual;
    private Boolean reSend;
    private Double invoiceAmount;
    private UUID hotel;
    private UUID agency;
    private String employee;

    public UpdateInvoiceCommand(UUID id,  LocalDateTime invoiceDate, Boolean isManual, Double invoiceAmount, UUID hotel, UUID agency, Status status, LocalDate dueDate, LocalDate resendDate, Boolean reSend, String employee) {
        this.id = id;
        this.invoiceDate = invoiceDate;
        this.isManual = isManual;
        this.invoiceAmount = invoiceAmount;
        this.hotel = hotel;
        this.agency = agency;
        this.reSend = reSend;
        this.dueDate = dueDate;
        this.reSendDate = resendDate;
        this.employee = employee;
        
    }

    public static UpdateInvoiceCommand fromRequest(UpdateInvoiceRequest request, UUID id) {
        return new UpdateInvoiceCommand(
                id, request.getInvoiceDate(), request.getIsManual(), request.getInvoiceAmount(), request.getHotel(), request.getAgency(),request.getStatus(), request.getDueDate(), request.getReSendDate() ,request.getReSend(),request.getEmployee()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateInvoiceMessage(id);
    }
}

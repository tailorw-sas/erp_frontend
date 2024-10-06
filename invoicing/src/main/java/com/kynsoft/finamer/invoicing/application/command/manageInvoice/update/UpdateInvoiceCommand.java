package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
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
    private UUID invoiceStatus;
    private String employee;

//    private LocalDateTime invoiceDate;
//    private LocalDate reSendDate;
//    private LocalDate dueDate;
//    private Boolean isManual;
//    private Boolean reSend;
//    private Double invoiceAmount;
//    private UUID hotel;
//    private UUID agency;
//    private String employee;

    public UpdateInvoiceCommand(UUID id,  LocalDateTime invoiceDate, UUID agency, String employee, UUID invoiceStatus) {
        this.id = id;
        this.invoiceDate = invoiceDate;
        this.agency = agency;
        this.invoiceStatus = invoiceStatus;
        this.employee = employee;
//        this.isManual = isManual;
//        this.invoiceAmount = invoiceAmount;
//        this.hotel = hotel;
//        this.reSend = reSend;
//        this.dueDate = dueDate;
//        this.reSendDate = resendDate;
        
    }

    public static UpdateInvoiceCommand fromRequest(UpdateInvoiceRequest request, UUID id) {
        return new UpdateInvoiceCommand(
                id, 
                request.getInvoiceDate(), request.getAgency(), request.getEmployee(), request.getInvoiceStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateInvoiceMessage(id);
    }
}

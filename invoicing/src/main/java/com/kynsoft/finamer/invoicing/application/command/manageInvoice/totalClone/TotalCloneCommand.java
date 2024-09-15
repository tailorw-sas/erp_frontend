package com.kynsoft.finamer.invoicing.application.command.manageInvoice.totalClone;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TotalCloneCommand implements ICommand {

    private UUID invoiceToClone;
    private UUID agency;
    private UUID hotel;
    private LocalDateTime invoiceDate;
    private UUID employeeId;
    private String employeeName;
    List<TotalCloneAttachmentRequest> attachments;
    List<TotalCloneBookingRequest> bookings;

    private UUID clonedInvoice;
    private Long clonedInvoiceId;
    private String clonedInvoiceNo;

    public TotalCloneCommand(UUID invoiceToClone, UUID agency, LocalDateTime invoiceDate,
                             UUID employeeId, String employeeName,
                             List<TotalCloneAttachmentRequest> attachments,
                             List<TotalCloneBookingRequest> bookings, UUID hotel) {

        this.clonedInvoice = UUID.randomUUID();
        this.invoiceToClone = invoiceToClone;
        this.agency = agency;
        this.invoiceDate = invoiceDate;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.attachments = attachments;
        this.bookings = bookings;
        this.hotel = hotel;
    }

    public static TotalCloneCommand fromRequest(TotalCloneRequest request){
        return new TotalCloneCommand(
                request.getInvoiceToClone(),
                request.getAgency(),
                request.getInvoiceDate(),
                request.getEmployeeId(),
                request.getEmployeeName(),
                request.attachments,
                request.bookings,
                request.getHotel()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new TotalCloneMessage(invoiceToClone, clonedInvoice, clonedInvoiceId, clonedInvoiceNo);
    }
}

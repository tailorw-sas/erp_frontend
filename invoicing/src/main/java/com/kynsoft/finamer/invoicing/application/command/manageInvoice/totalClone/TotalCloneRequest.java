package com.kynsoft.finamer.invoicing.application.command.manageInvoice.totalClone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TotalCloneRequest {

    private UUID invoiceToClone;
//    private UUID agency;
//    private UUID hotel;
//    private LocalDateTime invoiceDate;
//    private UUID employeeId;
    private String employeeName;
//    List<TotalCloneAttachmentRequest> attachments;
//    List<TotalCloneBookingRequest> bookings;
}

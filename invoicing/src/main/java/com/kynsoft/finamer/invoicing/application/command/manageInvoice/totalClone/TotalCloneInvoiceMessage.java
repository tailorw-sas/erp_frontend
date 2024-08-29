package com.kynsoft.finamer.invoicing.application.command.manageInvoice.totalClone;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingMessage;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TotalCloneInvoiceMessage implements ICommandMessage {

    private final String command = "TOTAL_CLONE_INVOICE";
    private UUID id;
    private List<CreateBookingMessage> bookingResponse;
    private List<CreateAttachmentMessage> attachmentMessages;
    private Long invoiceId;
    private String invoiceNo;

    public TotalCloneInvoiceMessage(UUID id, List<CreateBookingMessage> bookingResponse,
            List<CreateAttachmentMessage> attachmentMessages,
            Long invoiceId, String invoiceNo) {
        this.id = id;
        this.bookingResponse = bookingResponse;

        this.invoiceId = invoiceId;
        this.invoiceNo = invoiceNo;
        this.attachmentMessages = attachmentMessages;
    }

}

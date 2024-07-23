package com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingMessage;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateMessage;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateBulkInvoiceMessage implements ICommandMessage {

    private final String command = "CREATE_BULK_INVOICE";
    private UUID id;
    private List<CreateBookingMessage> bookingResponse;
    private List<CreateRoomRateMessage> roomRateMessages;
    private List<CreateAdjustmentMessage> adjustmentMessages;
    private Long invoiceId;

    public CreateBulkInvoiceMessage(UUID id, List<CreateBookingMessage> bookingResponse,
            List<CreateRoomRateMessage> roomRateMessages, List<CreateAdjustmentMessage> adjustmentMessages,
            Long invoiceId) {
        this.id = id;
        this.bookingResponse = bookingResponse;
        this.roomRateMessages = roomRateMessages;
        this.adjustmentMessages = adjustmentMessages;
        this.invoiceId = invoiceId;
    }

}

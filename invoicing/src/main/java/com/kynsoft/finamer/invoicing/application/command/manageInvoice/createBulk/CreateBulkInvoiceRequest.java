package com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk;

import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentRequest;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingRequest;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.create.CreateInvoiceRequest;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBulkInvoiceRequest {
    private CreateInvoiceRequest invoice;
    private List<CreateBookingRequest> bookings;
    private List<CreateRoomRateRequest> roomRates;
    private List<CreateAdjustmentRequest> adjustments;
    private List<CreateAttachmentRequest> attachments;
    private String employee;

}

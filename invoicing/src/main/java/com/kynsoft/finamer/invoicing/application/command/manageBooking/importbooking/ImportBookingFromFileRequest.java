package com.kynsoft.finamer.invoicing.application.command.manageBooking.importbooking;

import com.kynsoft.finamer.invoicing.domain.excel.ImportBookingRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImportBookingFromFileRequest {
    private ImportBookingRequest request;
}

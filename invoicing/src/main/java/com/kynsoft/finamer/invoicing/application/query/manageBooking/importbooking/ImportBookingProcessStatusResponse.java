package com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImportBookingProcessStatusResponse implements IResponse {

    private String status;
}

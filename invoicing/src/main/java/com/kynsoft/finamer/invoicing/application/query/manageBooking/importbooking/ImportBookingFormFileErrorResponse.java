package com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImportBookingFormFileErrorResponse implements IResponse {
    private PaginatedResponse paginatedResponse;
}

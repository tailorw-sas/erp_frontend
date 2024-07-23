package com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@AllArgsConstructor
public class ImportBookingFromFileErrorResponse implements IResponse {
    private PaginatedResponse paginatedResponse;

}

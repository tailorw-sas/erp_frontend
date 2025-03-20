package com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ImportBookingListRequest {
    private UUID importProcessId;
    private Pageable pageable;
}

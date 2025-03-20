package com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Getter
@Setter
public class ImportBookingListQuery implements IQuery {

    private UUID importProcessId;
    private Pageable pageable;

    public ImportBookingListQuery(String importProcessId, Pageable pageable){
        this.importProcessId = UUID.fromString(importProcessId);
        this.pageable = pageable;
    }
}

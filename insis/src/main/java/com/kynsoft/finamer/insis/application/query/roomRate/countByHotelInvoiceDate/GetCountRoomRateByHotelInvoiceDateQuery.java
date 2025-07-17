package com.kynsoft.finamer.insis.application.query.roomRate.countByHotelInvoiceDate;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GetCountRoomRateByHotelInvoiceDateQuery implements IQuery {

    private UUID employeeId;
    private LocalDate fromInvoiceDate;
    private LocalDate toInvoiceDate;
}

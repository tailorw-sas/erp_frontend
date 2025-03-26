package com.kynsoft.finamer.insis.application.query.objectResponse.roomRate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCountRoomRateByHotelInvoiceDateRequest {
    private LocalDate fromInvoiceDate;
    private LocalDate toInvoiceDate;
}

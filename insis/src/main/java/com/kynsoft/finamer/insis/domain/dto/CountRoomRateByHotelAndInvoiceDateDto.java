package com.kynsoft.finamer.insis.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountRoomRateByHotelAndInvoiceDateDto {
    private ManageHotelDto hotel;
    private LocalDate invoiceDate;
    private Long total;
}

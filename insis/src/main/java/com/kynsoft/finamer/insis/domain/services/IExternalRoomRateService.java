package com.kynsoft.finamer.insis.domain.services;

import com.kynsoft.finamer.insis.domain.dto.ExternalRoomRateDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IExternalRoomRateService {

    List<ExternalRoomRateDto> getTcaRoomRatesByInvoiceDateAndHotel(UUID processId,
                                                                   LocalDate invoiceDate,
                                                                   String hotel);

    Map<LocalDate,List<ExternalRoomRateDto>> getTcaRoomRatesBetweenInvoiceDateAndHotel(UUID processId,
                                                                                       LocalDate fromInvoiceDate,
                                                                                       LocalDate toInvoiceDate,
                                                                                       String hotel);
}

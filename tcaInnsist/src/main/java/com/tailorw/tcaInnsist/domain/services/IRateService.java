package com.tailorw.tcaInnsist.domain.services;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import com.tailorw.tcaInnsist.domain.dto.ManageRateDto;
import com.tailorw.tcaInnsist.domain.dto.RateDto;
import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import com.tailorw.tcaInnsist.infrastructure.model.Rate;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IRateService {

    PaginatedResponse searchRatesByBookingId(Pageable pageable, ManageConnectionDto configurationDto, String reservationNumber, String cuponNumber, String checkIn, String checkOut);

    List<Rate> findByCriteria(ManageHotelDto hotelDto, ManageConnectionDto configurationDto, List<FilterCriteria> filterCriteria);

    List<RateDto> findByInvoiceDate(ManageHotelDto hotelDto, ManageConnectionDto configurationDto, LocalDate invoiceDate);

    List<RateDto> findBetweenInvoiceDates(ManageHotelDto hotelDto, ManageConnectionDto configurationDto, LocalDate invoiceDateStart, LocalDate invoiceDateEnd);

    boolean validateRate(ManageRateDto newRate, ManageRateDto oldRate);

}

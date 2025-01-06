package com.tailorw.tcaInnsist.infrastructure.repository.interfaces;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.tailorw.tcaInnsist.infrastructure.model.Rate;
import com.tailorw.tcaInnsist.infrastructure.model.redis.ManageConnection;

import java.time.LocalDate;
import java.util.List;

public interface IRateRepository {
    List<Rate> findAllByBookingId(ManageConnection tcaConfigurationProperties, String reservationNumber, String cuponNumber, String checkInDate, String checkOutDate);

    List<Rate> findAllByCriteria(ManageConnection tcaConfigurationProperties, List<FilterCriteria> filterCriteria, String roomType);

    List<Rate> findByInvoiceCreatedAt(ManageConnection tcaConfigurationProperties, LocalDate invoiceDate, String roomType);

    List<Rate> findBetweenInvoiceDates(ManageConnection tcaConfigurationProperties, LocalDate invoiceDateStart, LocalDate invoiceDateEnd, String roomType);
}

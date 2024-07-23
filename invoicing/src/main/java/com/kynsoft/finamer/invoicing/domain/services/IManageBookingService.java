package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageBookingService {

    UUID create(ManageBookingDto dto);

    void update(ManageBookingDto dto);

    void calculateInvoiceAmount(ManageBookingDto dto);

    void delete(ManageBookingDto dto);
    boolean existByBookingHotelNumber(String bookingHotelNumber);

    ManageBookingDto findById(UUID id);

    boolean existsByExactLastTwoChars(String lastTwoChars, UUID hotelId);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<ManageBookingDto> findByIds(List<UUID> ids);
}

package com.kynsoft.finamer.insis.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.BookingDto;
import com.kynsoft.finamer.insis.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BookingStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IBookingService {
    UUID create(BookingDto dto);

    void update(BookingDto dto);

    void updateMany(List<BookingDto> dtoList);

    int updateAgencyByAgencyCode(ManageAgencyDto agencyDto);

    void delete(BookingDto dto);

    BookingDto findById(UUID id);

    List<BookingDto> findAllByIds(List<UUID> idList);

    BookingDto findByTcaId(ManageHotelDto hotelDto, LocalDate invoicingDate, String reservationNumber, String couponNumber);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

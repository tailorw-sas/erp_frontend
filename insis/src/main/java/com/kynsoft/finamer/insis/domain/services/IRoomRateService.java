package com.kynsoft.finamer.insis.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.BookingDto;
import com.kynsoft.finamer.insis.domain.dto.CountRoomRateByHotelAndInvoiceDateDto;
import com.kynsoft.finamer.insis.domain.dto.RoomRateDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IRoomRateService {
    UUID create(RoomRateDto dto);

    void createMany(List<RoomRateDto> rateDtoList);

    void update(RoomRateDto dto);

    void updateMany(List<RoomRateDto> rateDtoList);

    void delete(RoomRateDto dto);

    RoomRateDto findById(UUID id);

    RoomRateDto findByTcaId(RoomRateDto dto);

    List<RoomRateDto> findByHotelAndInvoiceDate(UUID hotelId, LocalDate invoiceDate);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<RoomRateDto> findAllAvailableByIds(List<UUID> idList);

    List<RoomRateDto> findAllByInvoiceId(UUID invoiceId);

    List<CountRoomRateByHotelAndInvoiceDateDto> countByHotelsAndInvoiceDate(List<UUID> idList, LocalDate invoiceFrom, LocalDate invoiceTo);
}

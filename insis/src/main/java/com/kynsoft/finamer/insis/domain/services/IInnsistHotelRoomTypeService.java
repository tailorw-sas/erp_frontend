package com.kynsoft.finamer.insis.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.InnsistHotelRoomTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IInnsistHotelRoomTypeService {
    UUID create(InnsistHotelRoomTypeDto dto);

    void update(InnsistHotelRoomTypeDto dto);

    void delete(InnsistHotelRoomTypeDto dto);

    InnsistHotelRoomTypeDto findById(UUID id);

    InnsistHotelRoomTypeDto findByHotelAndActive(UUID hotelId, String status);

    long countByRoomTypePrefixAndTradingCompanyId(UUID tradingCompanyId, String roomTypePrefix);

    List<InnsistHotelRoomTypeDto> findAll();

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

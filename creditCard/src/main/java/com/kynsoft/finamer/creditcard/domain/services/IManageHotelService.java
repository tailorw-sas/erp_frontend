package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageHotelService {
    UUID create(ManageHotelDto dto);

    void update(ManageHotelDto dto);

    void delete(ManageHotelDto dto);

    ManageHotelDto findById(UUID id);

    List<ManageHotelDto> findByIds(List<UUID> ids);

    List<ManageHotelDto> findAll();

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

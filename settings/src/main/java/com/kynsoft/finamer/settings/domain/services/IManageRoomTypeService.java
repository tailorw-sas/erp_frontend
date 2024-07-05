package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageRoomTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageRoomTypeService {

    UUID create(ManageRoomTypeDto dto);

    void update(ManageRoomTypeDto dto);

    void delete(ManageRoomTypeDto dto);

    ManageRoomTypeDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    Long countByCodeAndManageHotelIdAndNotId(String code, UUID manageHotelId, UUID id);
}

package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import java.util.List;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IManageRoomTypeService {

    UUID create(ManageRoomTypeDto dto);

    void update(ManageRoomTypeDto dto);

    void delete(ManageRoomTypeDto dto);

    ManageRoomTypeDto findById(UUID id);

    ManageRoomTypeDto findByCode(String code);

    boolean existByCode(String code);

    Long countByCodeAndNotId(String code, UUID id);

    Long countByCodeAndManageHotelIdAndNotId(String code, UUID manageHotelId, UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

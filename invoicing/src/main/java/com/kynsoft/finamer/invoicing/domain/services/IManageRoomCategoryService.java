package com.kynsoft.finamer.invoicing.domain.services;


import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomCategoryDto;
import java.util.List;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IManageRoomCategoryService {

    UUID create(ManageRoomCategoryDto dto);

    void update(ManageRoomCategoryDto dto);

    void delete(ManageRoomCategoryDto dto);

    ManageRoomCategoryDto findById(UUID id);

    Long countByCodeAndNotId(String code, UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

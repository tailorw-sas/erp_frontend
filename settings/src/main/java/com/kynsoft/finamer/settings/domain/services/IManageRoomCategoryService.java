package com.kynsoft.finamer.settings.domain.services;


import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageRoomCategoryDto;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface IManageRoomCategoryService {

    UUID create(ManageRoomCategoryDto dto);

    void update(ManageRoomCategoryDto dto);

    void delete(ManageRoomCategoryDto dto);

    ManageRoomCategoryDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);
}

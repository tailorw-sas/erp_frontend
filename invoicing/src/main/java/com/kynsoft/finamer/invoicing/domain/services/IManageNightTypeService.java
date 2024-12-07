package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageNightTypeDto;
import java.util.List;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IManageNightTypeService {
    UUID create(ManageNightTypeDto dto);

    void update(ManageNightTypeDto dto);

    void delete(ManageNightTypeDto dto);

    ManageNightTypeDto findById(UUID id);

    boolean existNightTypeByCode(String code);

    ManageNightTypeDto findByCode(String code);

    Long countByCodeAndNotId(String code, UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

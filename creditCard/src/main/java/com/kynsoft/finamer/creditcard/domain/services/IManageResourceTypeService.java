package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ResourceTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageResourceTypeService {
    UUID create(ResourceTypeDto dto);

    void update(ResourceTypeDto dto);

    void delete(ResourceTypeDto dto);

    ResourceTypeDto findById(UUID id);

    ResourceTypeDto findByCode(String code);

    Long countByCodeAndNotId(String code, UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);


}

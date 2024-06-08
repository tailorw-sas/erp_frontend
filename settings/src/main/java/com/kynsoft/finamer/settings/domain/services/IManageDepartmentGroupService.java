package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageDepartmentGroupDto;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageDepartmentGroupService {
    UUID create(ManageDepartmentGroupDto dto);

    void update(ManageDepartmentGroupDto dto);

    void delete(ManageDepartmentGroupDto dto);

    ManageDepartmentGroupDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);
}

package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeGroupDto;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageEmployeeGroupService {
    UUID create(ManageEmployeeGroupDto dto);

    void update(ManageEmployeeGroupDto dto);

    void delete(ManageEmployeeGroupDto dto);

    ManageEmployeeGroupDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);
}

package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePermissionDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManagePermissionService {

    UUID create(ManagePermissionDto dto);

    void update(ManagePermissionDto dto);

    void delete(ManagePermissionDto dto);

    ManagePermissionDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManagePermissionDto> findByIds(List<UUID> ids);
}

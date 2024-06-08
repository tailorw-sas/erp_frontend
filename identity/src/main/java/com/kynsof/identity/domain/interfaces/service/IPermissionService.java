package com.kynsof.identity.domain.interfaces.service;

import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IPermissionService {

    void create(PermissionDto dto);

    void update(PermissionDto dto);

    void delete(PermissionDto objectDto);

    void deleteAll(List<UUID> permissions);

    PermissionDto findById(UUID id);

    Long countByCodeAndNotId(String name, UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

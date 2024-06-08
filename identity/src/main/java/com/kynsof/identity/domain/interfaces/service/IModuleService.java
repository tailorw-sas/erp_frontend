package com.kynsof.identity.domain.interfaces.service;

import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.dto.moduleDto.ModuleNodeDto;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IModuleService {
    void create(ModuleDto object);
    void update(ModuleDto object);
    void delete(ModuleDto delete);
    void deleteAll(List<UUID> modules);
    ModuleDto findById(UUID id);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
    Long countByNameAndNotId(String name, UUID id);

    List<ModuleNodeDto> buildStructure();
}
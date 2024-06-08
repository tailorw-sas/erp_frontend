package com.kynsof.identity.domain.interfaces.service;

import com.kynsof.identity.domain.dto.BusinessModuleDto;
import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IBusinessModuleService {
    void create(BusinessModuleDto object);
    void create(List<BusinessModuleDto> objects);
    void update(List<BusinessModuleDto> objects);
    public List<BusinessModuleDto> findBusinessModuleByBusinessId(UUID businessId);
    void delete(BusinessModuleDto object);
    void delete(List<BusinessModuleDto> deletes);
    BusinessModuleDto findById(UUID id);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<ModuleDto> findModulesByBusinessId(UUID businessId);
    Long countByBussinessIdAndModuleId(UUID businessId, UUID moduleId);
}
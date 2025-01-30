package com.tailorw.finamer.scheduler.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.dto.ExecutionDateTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IExecutionDateTypeService {
    UUID create(ExecutionDateTypeDto dto);

    void update(ExecutionDateTypeDto dto);

    void delete(UUID id);

    ExecutionDateTypeDto getById(UUID id);

    ExecutionDateTypeDto getByCode(String code);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

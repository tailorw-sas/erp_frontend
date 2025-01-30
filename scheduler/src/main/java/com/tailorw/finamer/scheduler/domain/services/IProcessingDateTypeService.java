package com.tailorw.finamer.scheduler.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.dto.ProcessingDateTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IProcessingDateTypeService {
    UUID create(ProcessingDateTypeDto dto);

    void update(ProcessingDateTypeDto dto);

    void delete(UUID id);

    ProcessingDateTypeDto getById(UUID id);

    ProcessingDateTypeDto getByCode(String code);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

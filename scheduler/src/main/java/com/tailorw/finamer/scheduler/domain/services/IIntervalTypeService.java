package com.tailorw.finamer.scheduler.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.dto.IntervalTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IIntervalTypeService {
    UUID create(IntervalTypeDto dto);

    void update(IntervalTypeDto dto);

    void delete(UUID id);

    IntervalTypeDto getById(UUID id);

    IntervalTypeDto getByCode(String code);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

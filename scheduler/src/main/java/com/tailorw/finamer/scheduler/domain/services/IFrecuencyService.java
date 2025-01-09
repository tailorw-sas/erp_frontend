package com.tailorw.finamer.scheduler.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.dto.FrequencyDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IFrecuencyService {

    UUID create(FrequencyDto dto);

    void update(FrequencyDto dto);

    void delete(UUID id);

    FrequencyDto getById(UUID id);

    FrequencyDto getByCode(String code);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

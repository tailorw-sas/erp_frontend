package com.tailorw.finamer.scheduler.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerProcessingRuleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IBusinessProcessSchedulerProcessingRuleService {

    UUID create(BusinessProcessSchedulerProcessingRuleDto dto);

    void update(BusinessProcessSchedulerProcessingRuleDto dto);

    void delete(UUID id);

    BusinessProcessSchedulerProcessingRuleDto getById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

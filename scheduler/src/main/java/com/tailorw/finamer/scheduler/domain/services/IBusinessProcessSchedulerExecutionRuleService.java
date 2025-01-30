package com.tailorw.finamer.scheduler.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerExecutionRuleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IBusinessProcessSchedulerExecutionRuleService {

    UUID create(BusinessProcessSchedulerExecutionRuleDto dto);

    void update(BusinessProcessSchedulerExecutionRuleDto dto);

    void delete(UUID id);

    BusinessProcessSchedulerExecutionRuleDto getById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

package com.tailorw.finamer.scheduler.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerRuleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IBusinessProcessSchedulerRuleService {

    UUID create(BusinessProcessSchedulerRuleDto dto);

    void update(BusinessProcessSchedulerRuleDto dto);

    void delete(UUID id);

    BusinessProcessSchedulerRuleDto getById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

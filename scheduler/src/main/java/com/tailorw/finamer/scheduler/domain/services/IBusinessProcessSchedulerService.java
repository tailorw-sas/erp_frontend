package com.tailorw.finamer.scheduler.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerDto;
import com.tailorw.finamer.scheduler.infrastructure.model.Frequency;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IBusinessProcessSchedulerService {

    UUID create(BusinessProcessSchedulerDto dto);

    void update(BusinessProcessSchedulerDto dto);

    void delete(UUID id);

    BusinessProcessSchedulerDto findById(UUID id);

    List<BusinessProcessSchedulerDto> findByProcessId(UUID processId);

    List<BusinessProcessSchedulerDto> findByProcessIdAndStatus(UUID processId, Status status);

    List<BusinessProcessSchedulerDto> findByProcessIdAndStatusAndFrequency(UUID processId, Status status, UUID frequencyId);

    Long countActivesByProcessId(UUID id);

    PaginatedResponse search(List<FilterCriteria> filterCriteria, Pageable pageable);
}

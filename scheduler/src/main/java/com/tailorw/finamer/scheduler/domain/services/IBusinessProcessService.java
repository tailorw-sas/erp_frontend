package com.tailorw.finamer.scheduler.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IBusinessProcessService {

    UUID create(BusinessProcessDto dto);

    void update(BusinessProcessDto dto);

    BusinessProcessDto findById(UUID id);

    BusinessProcessDto findByCode(String code);

    BusinessProcessDto findByCodeAndByStatus(String code, String status);

    long countActiveAndInactiveBusinessProcessSchedulers(UUID id);

    PaginatedResponse search(List<FilterCriteria> filterCriteria, Pageable pageable);
}

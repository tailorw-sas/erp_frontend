package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import java.util.List;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IManageEmployeeService {
    UUID create(ManageEmployeeDto dto);

    void update(ManageEmployeeDto dto);

    void delete(ManageEmployeeDto dto);

    ManageEmployeeDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

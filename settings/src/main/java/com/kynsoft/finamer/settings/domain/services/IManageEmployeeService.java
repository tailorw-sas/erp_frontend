package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageEmployeeService {
    UUID create(ManageEmployeeDto dto);

    void update(ManageEmployeeDto dto);

    void delete(ManageEmployeeDto dto);

    ManageEmployeeDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByLoginNameAndNotId(String loginName, UUID id);

    Long countByEmailAndNotId(String email, UUID id);
}

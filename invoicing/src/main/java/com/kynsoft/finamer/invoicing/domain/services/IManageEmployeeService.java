package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageEmployeeDto;
import java.util.List;

import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IManageEmployeeService {
    UUID create(ManageEmployeeDto dto);

    void update(ManageEmployeeDto dto);

    void delete(ManageEmployeeDto dto);

    ManageEmployeeDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    String getEmployeeFullName(String employee);

    List<ManageEmployeeDto> findAllByIdsWithoutRelations(List<UUID> ids);

    Map<UUID, String> getEmployeeFullNameMapByIds(List<UUID> ids);

    List<UUID> findHotelsIdsByEmployeeId(String _employee_id);

    List<UUID> findAgencyIdsByEmployeeId(String _employee_id);
}

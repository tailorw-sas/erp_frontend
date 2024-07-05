package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageReportDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageReportService {

    UUID create(ManageReportDto dto);

    void update(ManageReportDto dto);

    void delete(ManageReportDto dto);

    ManageReportDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageReportDto> findByIds(List<UUID> ids);

    List<ManageReportDto> findAll();
}

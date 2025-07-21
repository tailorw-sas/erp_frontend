package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.JasperReportParameterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IReportParameterService {
    void create(JasperReportParameterDto object);

    void update(JasperReportParameterDto object);

    void delete(JasperReportParameterDto object);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    JasperReportParameterDto findById(UUID id);

    List<JasperReportParameterDto> findByTemplateId(UUID id);
}

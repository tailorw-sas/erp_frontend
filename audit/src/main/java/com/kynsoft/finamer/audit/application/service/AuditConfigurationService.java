package com.kynsoft.finamer.audit.application.service;

import com.kynsoft.finamer.audit.application.query.configuration.getbyid.GetConfigurationByIdResponse;
import com.kynsoft.finamer.audit.domain.dto.AuditConfigurationDto;
import com.kynsoft.finamer.audit.domain.request.FilterCriteria;
import com.kynsoft.finamer.audit.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuditConfigurationService {

    public Optional<AuditConfigurationDto> findByServiceNameAndRegisterId(String serviceName,UUID registerId);
    public PaginatedResponse findAll(Pageable pageable);

    public GetConfigurationByIdResponse findById(UUID id);
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria, String query);

    public void update(AuditConfigurationDto auditConfigurationDto);
    public void registerServiceAudit(AuditConfigurationDto auditConfigurationDto);
}

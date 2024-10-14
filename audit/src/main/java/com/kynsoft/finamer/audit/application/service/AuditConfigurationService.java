package com.kynsoft.finamer.audit.application.service;

import com.kynsoft.finamer.audit.application.command.configuration.update.UpdateConfigurationMessage;
import com.kynsoft.finamer.audit.application.command.configuration.update.UpdateConfigurationRequest;
import com.kynsoft.finamer.audit.application.query.configuration.getbyid.getConfigurationByIdResponse;
import com.kynsoft.finamer.audit.application.query.configuration.search.SearchConfigurationResponse;
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

    public getConfigurationByIdResponse findById(UUID id);
    public SearchConfigurationResponse search(Pageable pageable, List<FilterCriteria> filterCriteria, String query);

    public UpdateConfigurationMessage update(UUID id, UpdateConfigurationRequest updateConfigurationRequest);
    public void registerServiceAudit(AuditConfigurationDto auditConfigurationDto);
}

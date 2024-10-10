package com.kynsoft.finamer.audit.application.service;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.audit.application.command.configuration.update.UpdateConfigurationMessage;
import com.kynsoft.finamer.audit.application.command.configuration.update.UpdateConfigurationRequest;
import com.kynsoft.finamer.audit.application.query.configuration.getbyid.GetConfigurationByIdResponse;
import com.kynsoft.finamer.audit.application.query.configuration.search.SearchConfigurationResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AuditConfigurationService {

    public PaginatedResponse findAll(Pageable pageable);

    public GetConfigurationByIdResponse findById(UUID id);
    public SearchConfigurationResponse search(Pageable pageable, List<FilterCriteria> filterCriteria,String query);

    public UpdateConfigurationMessage update(UUID id, UpdateConfigurationRequest updateConfigurationRequest);

}

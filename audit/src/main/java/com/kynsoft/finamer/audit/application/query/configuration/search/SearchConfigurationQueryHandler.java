package com.kynsoft.finamer.audit.application.query.configuration.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.audit.application.service.AuditConfigurationService;
import org.springframework.stereotype.Component;

@Component
public class SearchConfigurationQueryHandler implements IQueryHandler<SearchConfigurationQuery,SearchConfigurationResponse> {

    private final AuditConfigurationService auditConfigurationService;

    public SearchConfigurationQueryHandler(AuditConfigurationService auditConfigurationService) {
        this.auditConfigurationService = auditConfigurationService;
    }

    @Override
    public SearchConfigurationResponse handle(SearchConfigurationQuery query) {
        return auditConfigurationService.search(query.getPageable(),query.getFilter(),query.getQuery());
    }
}

package com.kynsoft.finamer.audit.application.query.configuration.getbyid;


import com.kynsoft.finamer.audit.application.service.AuditConfigurationService;
import com.kynsoft.finamer.audit.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class GetConfigurationByIdQueryHandler implements IQueryHandler<GetConfigurationByIdQuery, GetConfigurationByIdResponse> {

    private final AuditConfigurationService auditConfigurationService;

    public GetConfigurationByIdQueryHandler(AuditConfigurationService auditConfigurationService) {
        this.auditConfigurationService = auditConfigurationService;
    }

    @Override
    public GetConfigurationByIdResponse handle(GetConfigurationByIdQuery query) {
       return auditConfigurationService.findById(query.getId());

    }
}

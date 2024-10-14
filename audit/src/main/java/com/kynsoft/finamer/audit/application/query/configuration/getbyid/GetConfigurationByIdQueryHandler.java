package com.kynsoft.finamer.audit.application.query.configuration.getbyid;


import com.kynsoft.finamer.audit.application.service.AuditConfigurationService;
import com.kynsoft.finamer.audit.domain.bus.query.IQueryHandler;

public class GetConfigurationByIdQueryHandler implements IQueryHandler<GetConfigurationByIdQuery, getConfigurationByIdResponse> {

    private final AuditConfigurationService auditConfigurationService;

    public GetConfigurationByIdQueryHandler(AuditConfigurationService auditConfigurationService) {
        this.auditConfigurationService = auditConfigurationService;
    }

    @Override
    public getConfigurationByIdResponse handle(GetConfigurationByIdQuery query) {
       return auditConfigurationService.findById(query.getId());

    }
}

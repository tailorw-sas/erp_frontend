package com.kynsoft.finamer.audit.application.query.configuration.getall;


import com.kynsoft.finamer.audit.application.service.AuditConfigurationService;
import com.kynsoft.finamer.audit.domain.bus.query.IQueryHandler;

public class GetAllConfigurationQueryHandler implements IQueryHandler<GetAllConfigurationQuery, GetAllConfigurationResponse> {

    private final AuditConfigurationService auditConfigurationService;

    public GetAllConfigurationQueryHandler(AuditConfigurationService auditConfigurationService) {
        this.auditConfigurationService = auditConfigurationService;
    }

    @Override
    public GetAllConfigurationResponse handle(GetAllConfigurationQuery query) {

       return new GetAllConfigurationResponse( auditConfigurationService.findAll(query.getPageable()));
    }
}

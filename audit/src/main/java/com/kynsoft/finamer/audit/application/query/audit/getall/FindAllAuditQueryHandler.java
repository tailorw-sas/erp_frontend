package com.kynsoft.finamer.audit.application.query.audit.getall;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.audit.application.service.AuditService;

public class FindAllAuditQueryHandler implements IQueryHandler<FindAllAuditQuery,IResponse> {

    private final AuditService auditService;

    public FindAllAuditQueryHandler(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public IResponse handle(FindAllAuditQuery query) {
        return auditService.findAll(query.getPageable());
    }
}

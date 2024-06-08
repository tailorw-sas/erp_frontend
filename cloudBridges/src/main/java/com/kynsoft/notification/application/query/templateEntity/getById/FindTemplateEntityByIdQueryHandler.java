package com.kynsoft.notification.application.query.templateEntity.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.notification.domain.dto.TemplateDto;
import com.kynsoft.notification.domain.service.ITemplateEntityService;
import org.springframework.stereotype.Component;

@Component
public class FindTemplateEntityByIdQueryHandler implements IQueryHandler<FindTemplateEntityByIdQuery, TemplateEntityResponse>  {

    private final ITemplateEntityService serviceImpl;

    public FindTemplateEntityByIdQueryHandler(ITemplateEntityService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public TemplateEntityResponse handle(FindTemplateEntityByIdQuery query) {
        TemplateDto patient = serviceImpl.findById(query.getId());
        return new TemplateEntityResponse(patient);
    }
}

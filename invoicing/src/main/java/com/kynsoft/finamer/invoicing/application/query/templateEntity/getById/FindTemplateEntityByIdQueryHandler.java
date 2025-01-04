package com.kynsoft.finamer.invoicing.application.query.templateEntity.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.domain.dto.TemplateDto;
import com.kynsoft.finamer.invoicing.domain.services.ITemplateEntityService;
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

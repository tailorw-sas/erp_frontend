package com.kynsoft.finamer.payment.application.query.templateEntity.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.ITemplateEntityService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchTemplateEntityQueryHandler implements IQueryHandler<GetSearchTemplateEntityQuery, PaginatedResponse>{

    private final ITemplateEntityService serviceImpl;

    public GetSearchTemplateEntityQueryHandler(ITemplateEntityService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public PaginatedResponse handle(GetSearchTemplateEntityQuery query) {

        return this.serviceImpl.search(query.getPageable(),query.getFilter());
    }
}

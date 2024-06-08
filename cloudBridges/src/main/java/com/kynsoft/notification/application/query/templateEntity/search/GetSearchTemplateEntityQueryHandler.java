package com.kynsoft.notification.application.query.templateEntity.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.notification.domain.service.ITemplateEntityService;
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

package com.kynsoft.notification.application.query.advertisingcontent.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.notification.domain.service.IAdvertisingContentService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchAdvertisingContentQueryHandler implements IQueryHandler<GetSearchAdvertisingContentQuery, PaginatedResponse>{
    private final IAdvertisingContentService service;
    
    public GetSearchAdvertisingContentQueryHandler(IAdvertisingContentService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchAdvertisingContentQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}

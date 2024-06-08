package com.kynsoft.notification.application.query.file.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.notification.domain.service.IAFileService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchAFileQueryHandler implements IQueryHandler<GetSearchAFileQuery, PaginatedResponse>{

    private final IAFileService serviceImpl;

    public GetSearchAFileQueryHandler(IAFileService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public PaginatedResponse handle(GetSearchAFileQuery query) {

        return this.serviceImpl.search(query.getPageable(),query.getFilter());
    }
}

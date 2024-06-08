package com.kynsoft.notification.application.query.mailjetConfiguration.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.notification.domain.service.IMailjetConfigurationService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchMailjetConfigurationQueryHandler implements IQueryHandler<GetSearchMailjetConfigurationQuery, PaginatedResponse>{

    private final IMailjetConfigurationService serviceImpl;

    public GetSearchMailjetConfigurationQueryHandler(IMailjetConfigurationService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public PaginatedResponse handle(GetSearchMailjetConfigurationQuery query) {

        return this.serviceImpl.search(query.getPageable(),query.getFilter());
    }
}

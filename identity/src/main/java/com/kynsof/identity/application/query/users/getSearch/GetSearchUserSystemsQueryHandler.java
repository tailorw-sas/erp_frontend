package com.kynsof.identity.application.query.users.getSearch;


import com.kynsof.identity.domain.interfaces.service.IUserSystemService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.stereotype.Component;

@Component
public class GetSearchUserSystemsQueryHandler implements IQueryHandler<GetSearchUserSystemsQuery, PaginatedResponse>{

    private final IUserSystemService serviceImpl;

    public GetSearchUserSystemsQueryHandler(IUserSystemService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public PaginatedResponse handle(GetSearchUserSystemsQuery query) {

        return this.serviceImpl.search(query.getPageable(),query.getFilter());
    }
}

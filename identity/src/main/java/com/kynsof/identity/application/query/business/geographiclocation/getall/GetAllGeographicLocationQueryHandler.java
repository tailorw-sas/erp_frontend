package com.kynsof.identity.application.query.business.geographiclocation.getall;

import com.kynsof.identity.domain.interfaces.service.IGeographicLocationService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.stereotype.Component;

@Component
public class GetAllGeographicLocationQueryHandler implements IQueryHandler<GetAllGeographicLocationQuery, PaginatedResponse>{

    private final IGeographicLocationService serviceImpl;

    public GetAllGeographicLocationQueryHandler(IGeographicLocationService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public PaginatedResponse handle(GetAllGeographicLocationQuery query) {

        return this.serviceImpl.findAll(query.getPageable());
    }
}

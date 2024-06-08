package com.kynsof.identity.application.query.business.geographiclocation.getbyid;

import com.kynsof.identity.application.query.business.geographiclocation.getall.GeographicLocationResponse;
import com.kynsof.identity.domain.dto.GeographicLocationDto;
import com.kynsof.identity.domain.interfaces.service.IGeographicLocationService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class FindByIdGeographicLocationQueryHandler implements IQueryHandler<FindByIdGeographicLocationQuery, GeographicLocationResponse>  {

    private final IGeographicLocationService serviceImpl;

    public FindByIdGeographicLocationQueryHandler(IGeographicLocationService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public GeographicLocationResponse handle(FindByIdGeographicLocationQuery query) {
        GeographicLocationDto geographicLocationDto = serviceImpl.findById(query.getId());

        return new GeographicLocationResponse(geographicLocationDto);
    }
}

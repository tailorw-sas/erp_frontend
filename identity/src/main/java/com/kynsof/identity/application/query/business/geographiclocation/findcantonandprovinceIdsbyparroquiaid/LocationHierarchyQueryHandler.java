package com.kynsof.identity.application.query.business.geographiclocation.findcantonandprovinceIdsbyparroquiaid;

import com.kynsof.identity.domain.dto.LocationHierarchyDto;
import com.kynsof.identity.domain.interfaces.service.IGeographicLocationService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class LocationHierarchyQueryHandler implements IQueryHandler<LocationHierarchyQuery, LocationHierarchyResponse>  {

    private final IGeographicLocationService serviceImpl;

    public LocationHierarchyQueryHandler(IGeographicLocationService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public LocationHierarchyResponse handle(LocationHierarchyQuery query) {
        LocationHierarchyDto contactInfoDto = serviceImpl.findCantonAndProvinceIdsByParroquiaId(query.getId());

        return new LocationHierarchyResponse(contactInfoDto);
    }
}

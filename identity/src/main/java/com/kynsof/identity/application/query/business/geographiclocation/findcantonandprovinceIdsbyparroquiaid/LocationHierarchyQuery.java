package com.kynsof.identity.application.query.business.geographiclocation.findcantonandprovinceIdsbyparroquiaid;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class LocationHierarchyQuery implements IQuery {

    private final UUID id;

    public LocationHierarchyQuery(UUID id) {
        this.id = id;
    }

}

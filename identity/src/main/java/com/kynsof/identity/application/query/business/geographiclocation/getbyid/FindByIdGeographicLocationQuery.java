package com.kynsof.identity.application.query.business.geographiclocation.getbyid;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindByIdGeographicLocationQuery implements IQuery {

    private final UUID id;

    public FindByIdGeographicLocationQuery(UUID id) {
        this.id = id;
    }

}

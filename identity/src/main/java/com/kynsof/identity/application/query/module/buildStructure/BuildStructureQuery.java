package com.kynsof.identity.application.query.module.buildStructure;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BuildStructureQuery implements IQuery {


    private final UUID businessId;


    public BuildStructureQuery(UUID businessId) {
        this.businessId = businessId;
    }

}

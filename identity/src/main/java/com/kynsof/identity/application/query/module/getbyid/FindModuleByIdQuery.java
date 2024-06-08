package com.kynsof.identity.application.query.module.getbyid;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindModuleByIdQuery  implements IQuery {

    private final UUID id;

    public FindModuleByIdQuery(UUID id) {
        this.id = id;
    }

}

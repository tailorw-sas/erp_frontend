package com.kynsof.identity.application.query.businessModule.getbyid;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindBusinessModuleByIdQuery  implements IQuery {

    private final UUID id;

    public FindBusinessModuleByIdQuery(UUID id) {
        this.id = id;
    }

}

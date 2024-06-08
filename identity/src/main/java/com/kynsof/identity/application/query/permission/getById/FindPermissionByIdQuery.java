package com.kynsof.identity.application.query.permission.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindPermissionByIdQuery  implements IQuery {

    private final UUID id;

    public FindPermissionByIdQuery(UUID id) {
        this.id = id;
    }

}

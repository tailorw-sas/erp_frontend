package com.kynsof.identity.application.query.users.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindByIdUserSystemsQuery implements IQuery {

    private final UUID id;

    public FindByIdUserSystemsQuery(UUID id) {
        this.id = id;
    }

}

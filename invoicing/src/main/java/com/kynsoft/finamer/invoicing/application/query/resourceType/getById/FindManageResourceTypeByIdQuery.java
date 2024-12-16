package com.kynsoft.finamer.invoicing.application.query.resourceType.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManageResourceTypeByIdQuery  implements IQuery {

    private final UUID id;

    public FindManageResourceTypeByIdQuery(UUID id) {
        this.id = id;
    }

}

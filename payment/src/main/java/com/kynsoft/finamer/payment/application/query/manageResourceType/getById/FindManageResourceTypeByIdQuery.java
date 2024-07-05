package com.kynsoft.finamer.payment.application.query.manageResourceType.getById;

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

package com.kynsoft.finamer.settings.application.query.managerChargeType.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagerChargeTypeByIdQuery  implements IQuery {

    private final UUID id;

    public FindManagerChargeTypeByIdQuery(UUID id) {
        this.id = id;
    }

}

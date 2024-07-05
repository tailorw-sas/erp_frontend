package com.kynsoft.finamer.invoicing.application.query.manageAdjustment.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindAdjustmentByIdQuery  implements IQuery {

    private final UUID id;

    public FindAdjustmentByIdQuery(UUID id) {
        this.id = id;
    }

}

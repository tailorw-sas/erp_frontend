package com.kynsoft.finamer.invoicing.application.query.incomeAdjustment.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindIncomeAdjustmentByIdQuery  implements IQuery {

    private final UUID id;

    public FindIncomeAdjustmentByIdQuery(UUID id) {
        this.id = id;
    }

}

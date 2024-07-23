package com.kynsoft.finamer.invoicing.application.query.income.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindIncomeByIdQuery  implements IQuery {

    private final UUID id;

    public FindIncomeByIdQuery(UUID id) {
        this.id = id;
    }

}

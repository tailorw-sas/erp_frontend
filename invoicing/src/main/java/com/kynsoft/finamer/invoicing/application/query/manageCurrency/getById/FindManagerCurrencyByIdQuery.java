package com.kynsoft.finamer.invoicing.application.query.manageCurrency.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagerCurrencyByIdQuery  implements IQuery {

    private final UUID id;

    public FindManagerCurrencyByIdQuery(UUID id) {
        this.id = id;
    }

}

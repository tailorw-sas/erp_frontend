package com.kynsoft.finamer.settings.application.query.managerMerchantCurrency.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagerMerchantCurrencyByIdQuery  implements IQuery {

    private final UUID id;

    public FindManagerMerchantCurrencyByIdQuery(UUID id) {
        this.id = id;
    }

}

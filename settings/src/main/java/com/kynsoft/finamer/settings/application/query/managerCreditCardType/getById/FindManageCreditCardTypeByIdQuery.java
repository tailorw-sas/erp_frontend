package com.kynsoft.finamer.settings.application.query.managerCreditCardType.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManageCreditCardTypeByIdQuery  implements IQuery {

    private final UUID id;

    public FindManageCreditCardTypeByIdQuery(UUID id) {
        this.id = id;
    }

}

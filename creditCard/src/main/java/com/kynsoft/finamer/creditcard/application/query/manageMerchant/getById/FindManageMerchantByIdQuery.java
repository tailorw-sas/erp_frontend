package com.kynsoft.finamer.creditcard.application.query.manageMerchant.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManageMerchantByIdQuery  implements IQuery {

    private final UUID id;

    public FindManageMerchantByIdQuery(UUID id) {
        this.id = id;
    }

}

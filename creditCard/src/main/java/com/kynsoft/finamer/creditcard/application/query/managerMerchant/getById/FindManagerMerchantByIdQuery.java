package com.kynsoft.finamer.creditcard.application.query.managerMerchant.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagerMerchantByIdQuery  implements IQuery {

    private final UUID id;
    private final UUID transactionUuid;

    public FindManagerMerchantByIdQuery(UUID id, UUID transactionUuid) {
        this.id = id;
        this.transactionUuid = transactionUuid;
    }

}

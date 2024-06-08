package com.kynsoft.finamer.settings.application.query.managerMerchant.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagerMerchantByIdQuery  implements IQuery {

    private final UUID id;

    public FindManagerMerchantByIdQuery(UUID id) {
        this.id = id;
    }

}

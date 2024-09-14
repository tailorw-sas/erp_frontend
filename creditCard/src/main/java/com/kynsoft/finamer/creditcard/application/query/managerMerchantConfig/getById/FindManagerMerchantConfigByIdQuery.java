package com.kynsoft.finamer.creditcard.application.query.managerMerchantConfig.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Data;

import java.util.UUID;

@Data
public class FindManagerMerchantConfigByIdQuery implements IQuery {

    private final UUID id;

    public FindManagerMerchantConfigByIdQuery(UUID id) {
        this.id = id;
    }
}

package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindMerchantLanguageCodeByIdQuery implements IQuery {

    private final UUID id;

    public FindMerchantLanguageCodeByIdQuery(UUID id) {
        this.id = id;
    }

}

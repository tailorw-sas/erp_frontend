package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.findMerchantLanguage;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindMerchantLanguageQuery implements IQuery {
    private final UUID merchantId;
    private final UUID languageId;
}

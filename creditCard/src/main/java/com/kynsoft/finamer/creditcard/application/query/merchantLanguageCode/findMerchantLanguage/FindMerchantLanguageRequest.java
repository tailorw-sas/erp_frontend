package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.findMerchantLanguage;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;


@Getter
@AllArgsConstructor
public class FindMerchantLanguageRequest {
    private final UUID merchantId;
    private final UUID languageId;
}

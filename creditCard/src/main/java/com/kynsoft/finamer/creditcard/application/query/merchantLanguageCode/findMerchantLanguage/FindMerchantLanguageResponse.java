package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.findMerchantLanguage;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindMerchantLanguageResponse implements IResponse {
    private final String merchantLanguage;
}

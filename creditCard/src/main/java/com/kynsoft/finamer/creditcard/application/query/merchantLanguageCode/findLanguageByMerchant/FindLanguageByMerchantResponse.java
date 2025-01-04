package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.findLanguageByMerchant;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FindLanguageByMerchantResponse implements IResponse {
    List<FindLanguageByMerchantLanguageResponse> languages;
}

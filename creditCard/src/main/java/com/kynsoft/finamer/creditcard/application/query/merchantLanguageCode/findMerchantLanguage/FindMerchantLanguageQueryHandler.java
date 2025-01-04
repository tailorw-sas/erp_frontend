package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.findMerchantLanguage;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.domain.services.IMerchantLanguageCodeService;
import org.springframework.stereotype.Component;

@Component
public class FindMerchantLanguageQueryHandler implements IQueryHandler<FindMerchantLanguageQuery, FindMerchantLanguageResponse> {

    private final IMerchantLanguageCodeService merchantLanguageCodeService;

    public FindMerchantLanguageQueryHandler(IMerchantLanguageCodeService merchantLanguageCodeService) {
        this.merchantLanguageCodeService = merchantLanguageCodeService;
    }

    @Override
    public FindMerchantLanguageResponse handle(FindMerchantLanguageQuery query) {
        return new FindMerchantLanguageResponse(this.merchantLanguageCodeService.findMerchantLanguageByMerchantIdAndLanguageId(query.getMerchantId(), query.getLanguageId()));
    }
}

package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.findLanguageByMerchant;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.domain.services.IMerchantLanguageCodeService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FindLanguagesByMerchantQueryHandler implements IQueryHandler<FindLanguageByMerchantQuery, FindLanguageByMerchantResponse> {

    private final IMerchantLanguageCodeService merchantLanguageCodeService;

    public FindLanguagesByMerchantQueryHandler(IMerchantLanguageCodeService merchantLanguageCodeService) {
        this.merchantLanguageCodeService = merchantLanguageCodeService;
    }

    @Override
    public FindLanguageByMerchantResponse handle(FindLanguageByMerchantQuery query) {
        List<FindLanguageByMerchantLanguageResponse> languageResponses =
                this.merchantLanguageCodeService.findManageLanguageByMerchantId(query.getMerchantId())
                        .stream().map(FindLanguageByMerchantLanguageResponse::new)
                        .toList();

        return new FindLanguageByMerchantResponse(languageResponses);
    }
}

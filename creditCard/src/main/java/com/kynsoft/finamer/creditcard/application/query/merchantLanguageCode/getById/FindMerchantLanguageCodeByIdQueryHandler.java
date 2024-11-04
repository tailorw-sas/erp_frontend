package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.MerchantLanguageCodeResponse;
import com.kynsoft.finamer.creditcard.domain.dto.MerchantLanguageCodeDto;
import com.kynsoft.finamer.creditcard.domain.services.IMerchantLanguageCodeService;
import org.springframework.stereotype.Component;

@Component
public class FindMerchantLanguageCodeByIdQueryHandler implements IQueryHandler<FindMerchantLanguageCodeByIdQuery, MerchantLanguageCodeResponse>  {

    private final IMerchantLanguageCodeService service;

    public FindMerchantLanguageCodeByIdQueryHandler(IMerchantLanguageCodeService service) {
        this.service = service;
    }

    @Override
    public MerchantLanguageCodeResponse handle(FindMerchantLanguageCodeByIdQuery query) {
        MerchantLanguageCodeDto response = service.findById(query.getId());

        return new MerchantLanguageCodeResponse(response);
    }
}

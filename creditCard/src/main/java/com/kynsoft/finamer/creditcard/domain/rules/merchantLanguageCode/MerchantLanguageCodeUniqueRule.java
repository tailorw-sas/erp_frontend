package com.kynsoft.finamer.creditcard.domain.rules.merchantLanguageCode;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IMerchantLanguageCodeService;

import java.util.UUID;

public class MerchantLanguageCodeUniqueRule extends BusinessRule {

    private final IMerchantLanguageCodeService merchantLanguageCodeService;
    private final UUID manageMerchant;
    private final UUID manageLanguage;
    private final UUID id;

    public MerchantLanguageCodeUniqueRule(IMerchantLanguageCodeService merchantLanguageCodeService, UUID manageMerchant, UUID manageLanguage, UUID id) {
        super(
                DomainErrorMessage.MERCHANT_LANGUAGE_MUST_BE_UNIQUE,
                new ErrorField("merchantLanguage", DomainErrorMessage.MERCHANT_LANGUAGE_MUST_BE_UNIQUE.getReasonPhrase()));
        this.merchantLanguageCodeService = merchantLanguageCodeService;
        this.manageMerchant = manageMerchant;
        this.manageLanguage = manageLanguage;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.merchantLanguageCodeService.countByManageMerchantAndMerchantLanguageAndNotId(
                manageMerchant, manageLanguage, id
        ) > 0;
    }
}

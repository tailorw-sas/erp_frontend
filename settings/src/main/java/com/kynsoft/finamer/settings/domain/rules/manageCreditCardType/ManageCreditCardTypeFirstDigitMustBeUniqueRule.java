package com.kynsoft.finamer.settings.domain.rules.manageCreditCardType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageCreditCardTypeService;

import java.util.UUID;

public class ManageCreditCardTypeFirstDigitMustBeUniqueRule extends BusinessRule {

    private final IManageCreditCardTypeService service;

    private final Integer firstDigit;

    private final UUID id;

    public ManageCreditCardTypeFirstDigitMustBeUniqueRule(IManageCreditCardTypeService service, Integer firstDigit, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("firstDigit", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
        );
        this.service = service;
        this.firstDigit = firstDigit;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByFirstDigitAndNotId(firstDigit, id) > 0;
    }

}

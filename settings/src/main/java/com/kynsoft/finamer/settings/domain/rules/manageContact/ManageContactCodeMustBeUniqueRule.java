package com.kynsoft.finamer.settings.domain.rules.manageContact;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageContactService;

import java.util.UUID;

public class ManageContactCodeMustBeUniqueRule extends BusinessRule {

    private final IManageContactService service;

    private final String code;

    private final UUID manageHotelId;

    private final UUID id;

    public ManageContactCodeMustBeUniqueRule(IManageContactService service, String code, UUID manageHotelId, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("code", DomainErrorMessage.ITEM_ALREADY_EXITS.toString())
        );
        this.service = service;
        this.code = code;
        this.manageHotelId = manageHotelId;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCodeAndManageHotelIdAndNotId(code, manageHotelId, id) > 0;
    }
}

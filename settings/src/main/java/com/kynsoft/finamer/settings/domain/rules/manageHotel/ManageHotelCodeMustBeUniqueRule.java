package com.kynsoft.finamer.settings.domain.rules.manageHotel;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;

import java.util.UUID;

public class ManageHotelCodeMustBeUniqueRule extends BusinessRule {

    private final IManageHotelService service;

    private final String code;

    private final UUID id;

    public ManageHotelCodeMustBeUniqueRule(IManageHotelService service, String code, UUID id) {
        super(
                DomainErrorMessage.MANAGE_HOTEL_CODE_MUST_BY_UNIQUE,
                new ErrorField("code", "The code must be unique.")
        );
        this.service = service;
        this.code = code;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCodeAndNotId(code, id) > 0;
    }

}

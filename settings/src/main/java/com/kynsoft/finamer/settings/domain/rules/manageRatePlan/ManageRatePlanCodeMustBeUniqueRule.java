package com.kynsoft.finamer.settings.domain.rules.manageRatePlan;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageRatePlanService;

import java.util.UUID;

public class ManageRatePlanCodeMustBeUniqueRule extends BusinessRule {

    private final IManageRatePlanService service;

    private final String code;

    private final UUID id;
    private final UUID hotelId;

    public ManageRatePlanCodeMustBeUniqueRule(IManageRatePlanService service, String code, UUID id, UUID hotelId) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("code", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
        );
        this.service = service;
        this.code = code;
        this.id = id;
        this.hotelId = hotelId;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCodeAndNotId(code, id, hotelId) > 0;
    }

}

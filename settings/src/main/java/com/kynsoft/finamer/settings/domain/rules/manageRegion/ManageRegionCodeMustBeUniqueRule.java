package com.kynsoft.finamer.settings.domain.rules.manageRegion;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageRegionService;

import java.util.UUID;

public class ManageRegionCodeMustBeUniqueRule extends BusinessRule {

    private final IManageRegionService service;

    private final String code;

    private final UUID id;

    public ManageRegionCodeMustBeUniqueRule(IManageRegionService service, String code, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("code", DomainErrorMessage.ITEM_ALREADY_EXITS.toString())
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

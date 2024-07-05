package com.kynsoft.finamer.settings.domain.rules.manageRoomCategory;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyTypeService;
import com.kynsoft.finamer.settings.domain.services.IManageRoomCategoryService;

import java.util.UUID;

public class ManageRoomCategoryCodeMustBeUniqueRule extends BusinessRule {

    private final IManageRoomCategoryService service;

    private final String code;

    private final UUID id;

    public ManageRoomCategoryCodeMustBeUniqueRule(IManageRoomCategoryService service, String code, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("code", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
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

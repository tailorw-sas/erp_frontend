package com.kynsoft.finamer.settings.domain.rules.manageRoomType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageRoomTypeService;

import java.util.UUID;

public class ManageRoomTypeCodeMustBeUniqueRule extends BusinessRule {

    private final IManageRoomTypeService service;

    private final String code;

    private final UUID manageHotelId;

    private final UUID id;

    public ManageRoomTypeCodeMustBeUniqueRule(IManageRoomTypeService service, String code, UUID manageHotelId, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("code", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
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

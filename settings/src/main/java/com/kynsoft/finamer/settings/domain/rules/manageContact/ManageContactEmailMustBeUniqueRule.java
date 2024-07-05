package com.kynsoft.finamer.settings.domain.rules.manageContact;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageContactService;

import java.util.UUID;

public class ManageContactEmailMustBeUniqueRule extends BusinessRule {

    private final IManageContactService service;

    private final String email;

    private final UUID manageHotelId;

    private final UUID id;


    public ManageContactEmailMustBeUniqueRule(IManageContactService service, String email, UUID manageHotelId, UUID id) {
        super(
                DomainErrorMessage.EMAIL_ALREADY_EXISTS,
                new ErrorField("email", "Email already exists")
        );
        this.service = service;
        this.email = email;
        this.manageHotelId = manageHotelId;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByEmailAndManageHotelIdAndNotId(email, manageHotelId, id) > 0;
    }
}

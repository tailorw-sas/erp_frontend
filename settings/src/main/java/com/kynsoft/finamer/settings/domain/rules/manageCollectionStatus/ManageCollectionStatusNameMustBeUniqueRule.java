package com.kynsoft.finamer.settings.domain.rules.manageCollectionStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageCollectionStatusService;

import java.util.UUID;

public class ManageCollectionStatusNameMustBeUniqueRule extends BusinessRule {
    private final IManageCollectionStatusService service;

    private final String name;

    private final UUID id;

    public ManageCollectionStatusNameMustBeUniqueRule(IManageCollectionStatusService service, String name, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("name", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
        );
        this.service = service;
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByNameAndNotId(name, id) > 0;
    }
}

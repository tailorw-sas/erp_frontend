package com.kynsoft.finamer.settings.domain.rules.managerPaymentStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagerPaymentStatusService;

import java.util.UUID;

public class ManagePaymentStatusAppliedMustBeUniqueRule extends BusinessRule {

    private final IManagerPaymentStatusService service;

    private final UUID id;

    public ManagePaymentStatusAppliedMustBeUniqueRule(IManagerPaymentStatusService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_PAYMENT_STATUS_TO_APPLIED,
                new ErrorField("applied", DomainErrorMessage.MANAGE_PAYMENT_STATUS_TO_APPLIED.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        Long cant = this.service.countByAppliedAndNotId(id);
        System.err.println("##################################################");
        System.err.println("##################################################");
        System.err.println("Cant: " + cant);
        System.err.println("ID: " + id.toString());
        System.err.println("##################################################");
        System.err.println("##################################################");
        return cant > 0;
    }

}

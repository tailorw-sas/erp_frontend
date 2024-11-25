package com.kynsoft.finamer.creditcard.domain.rules.manageHotelPaymentStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelPaymentStatusService;

import java.util.UUID;

public class ManageHotelPaymentStatusCompletedMustBeUniqueRule extends BusinessRule {

    private final IManageHotelPaymentStatusService service;

    private final UUID id;

    public ManageHotelPaymentStatusCompletedMustBeUniqueRule(IManageHotelPaymentStatusService service,
                                                             UUID id) {
        super(
                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_CHECK_COMPLETED,
                new ErrorField("completed", DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_CHECK_COMPLETED.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCompletedAndNotId(id) > 0;
    }

}

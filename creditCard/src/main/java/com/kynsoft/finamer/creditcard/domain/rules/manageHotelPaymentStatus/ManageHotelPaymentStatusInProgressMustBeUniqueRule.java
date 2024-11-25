package com.kynsoft.finamer.creditcard.domain.rules.manageHotelPaymentStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelPaymentStatusService;

import java.util.UUID;

public class ManageHotelPaymentStatusInProgressMustBeUniqueRule extends BusinessRule {

    private final IManageHotelPaymentStatusService service;

    private final UUID id;

    public ManageHotelPaymentStatusInProgressMustBeUniqueRule(IManageHotelPaymentStatusService service,
                                                              UUID id) {
        super(
                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_CHECK_IN_PROGRESS,
                new ErrorField("inProgress", DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_CHECK_IN_PROGRESS.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByInProgressAndNotId(id) > 0;
    }

}

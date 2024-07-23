package com.kynsoft.finamer.payment.domain.rules.closeOperation;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;

import java.util.UUID;

public class CloseOperationHotelMustBeUniqueRule extends BusinessRule {

    private final IPaymentCloseOperationService service;

    private final UUID hotelId;

    public CloseOperationHotelMustBeUniqueRule(IPaymentCloseOperationService service, UUID hotelId) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("hotel", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
        );
        this.service = service;
        this.hotelId = hotelId;
    }

    @Override
    public boolean isBroken() {
        return this.service.findByHotelId(hotelId) > 0;
    }

}

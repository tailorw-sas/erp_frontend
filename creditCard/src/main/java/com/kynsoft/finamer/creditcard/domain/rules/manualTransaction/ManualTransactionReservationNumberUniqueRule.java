package com.kynsoft.finamer.creditcard.domain.rules.manualTransaction;


import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;

import java.util.UUID;

public class ManualTransactionReservationNumberUniqueRule extends BusinessRule {

    private final ITransactionService service;

    private final String reservationNumber;

    private final UUID hotel;

    public ManualTransactionReservationNumberUniqueRule(ITransactionService service, String reservationNumber, UUID hotel) {
        super(
                DomainErrorMessage.RESERVATION_NUMBER_MUST_BE_UNIQUE,
                new ErrorField("reservationNumber", DomainErrorMessage.RESERVATION_NUMBER_MUST_BE_UNIQUE.getReasonPhrase())
        );
        this.service = service;
        this.reservationNumber = reservationNumber;
        this.hotel = hotel;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByReservationNumberAndManageHotelIdAndNotId(reservationNumber, hotel) > 0;
    }
}

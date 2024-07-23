package com.kynsoft.finamer.creditcard.domain.rules.manualTransaction;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.dto.CreditCardCloseOperationDto;
import com.kynsoft.finamer.creditcard.domain.services.ICreditCardCloseOperationService;

import java.time.LocalDate;
import java.util.UUID;

public class ManualTransactionCheckInCloseOperationRule extends BusinessRule {

    private final ICreditCardCloseOperationService closeOperationService;

    private final LocalDate checkIn;

    private final UUID hotel;

    public ManualTransactionCheckInCloseOperationRule(ICreditCardCloseOperationService closeOperationService, LocalDate checkIn, UUID hotel) {
        super(
                DomainErrorMessage.VCC_CLOSE_OPERATION_OUT_OF_RANGE,
                new ErrorField("checkIn", DomainErrorMessage.VCC_CLOSE_OPERATION_OUT_OF_RANGE.getReasonPhrase())
        );
        this.closeOperationService = closeOperationService;
        this.checkIn = checkIn;
        this.hotel = hotel;
    }

    @Override
    public boolean isBroken() {
        CreditCardCloseOperationDto dto = this.closeOperationService.findActiveByHotelId(hotel);
        return checkIn.isBefore(dto.getBeginDate()) || checkIn.isAfter(dto.getEndDate());
    }
}

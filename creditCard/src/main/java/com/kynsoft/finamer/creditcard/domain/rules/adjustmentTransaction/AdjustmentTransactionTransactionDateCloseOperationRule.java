package com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.dto.CreditCardCloseOperationDto;
import com.kynsoft.finamer.creditcard.domain.services.ICreditCardCloseOperationService;

import java.time.LocalDate;
import java.util.UUID;

public class AdjustmentTransactionTransactionDateCloseOperationRule extends BusinessRule {

    private final ICreditCardCloseOperationService closeOperationService;

    private final LocalDate transactionDate;

    private final UUID hotel;

    public AdjustmentTransactionTransactionDateCloseOperationRule(ICreditCardCloseOperationService closeOperationService, LocalDate transactionDate, UUID hotel) {
        super(
                DomainErrorMessage.VCC_CLOSE_OPERATION_OUT_OF_RANGE,
                new ErrorField("transactionDate", DomainErrorMessage.VCC_CLOSE_OPERATION_OUT_OF_RANGE.getReasonPhrase())
        );
        this.closeOperationService = closeOperationService;
        this.transactionDate = transactionDate;
        this.hotel = hotel;
    }

    @Override
    public boolean isBroken() {
        CreditCardCloseOperationDto dto = this.closeOperationService.findActiveByHotelId(hotel);
        return transactionDate.isBefore(dto.getBeginDate()) || transactionDate.isAfter(dto.getEndDate());
    }
}

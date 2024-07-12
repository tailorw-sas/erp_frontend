package com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCreditCardCloseOperationRequest {

    private Status status;
    private UUID hotel;
    private LocalDate beginDate;
    private LocalDate endDate;
}

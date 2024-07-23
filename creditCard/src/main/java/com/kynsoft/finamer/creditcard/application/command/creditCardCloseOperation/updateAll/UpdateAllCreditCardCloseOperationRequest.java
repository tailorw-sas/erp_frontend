package com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.updateAll;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAllCreditCardCloseOperationRequest {

    private List<UUID> hotels;
    private Status status;
    private LocalDate beginDate;
    private LocalDate endDate;
}

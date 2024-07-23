package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.updateAll;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAllPaymentCloseOperationRequest {

    private List<UUID> hotels;
    private Status status;
    private LocalDate beginDate;
    private LocalDate endDate;
}

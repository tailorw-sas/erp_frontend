package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.update;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePaymentCloseOperationRequest {

    private Status status;
    private UUID hotel;
    private LocalDate beginDate;
    private LocalDate endDate;
}

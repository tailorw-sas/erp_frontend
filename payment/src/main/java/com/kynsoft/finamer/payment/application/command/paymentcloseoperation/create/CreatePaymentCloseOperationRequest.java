package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.create;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentCloseOperationRequest {

    private Status status;
    private UUID hotel;
    private LocalDate beginDate;
    private LocalDate endDate;
}

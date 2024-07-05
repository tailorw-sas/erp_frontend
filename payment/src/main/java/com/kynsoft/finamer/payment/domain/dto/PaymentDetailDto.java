package com.kynsoft.finamer.payment.domain.dto;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentDetailDto {

    private UUID id;
    private Status status;
    private PaymentDto payment;
    private ManagePaymentTransactionTypeDto transactionType;
    private Double amount;
    private String remark;
    private PaymentDetailDto parent;

}

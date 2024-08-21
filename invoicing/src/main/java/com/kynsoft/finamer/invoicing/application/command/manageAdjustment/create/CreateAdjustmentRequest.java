package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAdjustmentRequest {

    private Double amount;
    private LocalDateTime date;
    private String description;
    private UUID transactionType;
    private UUID paymentTransactionType;
    private UUID roomRate;
    private String employee;
}

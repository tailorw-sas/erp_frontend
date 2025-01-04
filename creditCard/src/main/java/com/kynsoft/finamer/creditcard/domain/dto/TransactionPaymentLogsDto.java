package com.kynsoft.finamer.creditcard.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionPaymentLogsDto {

    private UUID id;
    private UUID transactionId;
    private String merchantRequest;
    private String merchantResponse;
    private Boolean isProcessed;
    private Long transactionNumber;
}

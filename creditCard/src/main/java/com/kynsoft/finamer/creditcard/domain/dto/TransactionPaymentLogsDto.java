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
    private UUID transactionUuid;
    private String html;
    private String merchantReturn;



}

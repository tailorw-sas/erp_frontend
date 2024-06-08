package com.kynsof.identity.application.command.walletTransaction.create;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateWalletTransactionRequest {
    private BigDecimal amount;
    private String description;
    private String requestId;
    private String authorizationCode;
}

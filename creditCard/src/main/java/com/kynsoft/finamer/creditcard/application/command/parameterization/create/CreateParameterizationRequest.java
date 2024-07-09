package com.kynsoft.finamer.creditcard.application.command.parameterization.create;

import lombok.Getter;

@Getter
public class CreateParameterizationRequest {

    private String transactionStatusCode;
    private String transactionCategory;
    private String transactionSubCategory;
}

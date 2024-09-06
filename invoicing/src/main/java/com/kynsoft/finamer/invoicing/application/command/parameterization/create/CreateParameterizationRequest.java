package com.kynsoft.finamer.invoicing.application.command.parameterization.create;

import lombok.Getter;

@Getter
public class CreateParameterizationRequest {

    private String sent;
    private String reconciled;
    private String processed;
    private String canceled;
    private String pending;
}

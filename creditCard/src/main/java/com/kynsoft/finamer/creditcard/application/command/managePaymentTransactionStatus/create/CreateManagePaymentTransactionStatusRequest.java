package com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.create;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateManagePaymentTransactionStatusRequest {

    private String code;
    private String name;
    private Status status;
    private String description;
    private boolean requireValidation;
    private List<UUID> navigate;

    private boolean inProgress;
    private boolean completed;
    private boolean cancelled;
    private boolean applied;
}

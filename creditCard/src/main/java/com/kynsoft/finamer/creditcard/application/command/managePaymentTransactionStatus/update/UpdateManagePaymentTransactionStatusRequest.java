package com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateManagePaymentTransactionStatusRequest {

    private UUID id;
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

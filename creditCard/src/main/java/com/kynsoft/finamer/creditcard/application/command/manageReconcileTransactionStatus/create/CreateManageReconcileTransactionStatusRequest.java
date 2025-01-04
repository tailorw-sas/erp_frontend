package com.kynsoft.finamer.creditcard.application.command.manageReconcileTransactionStatus.create;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateManageReconcileTransactionStatusRequest {

    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean requireValidation;
    private List<UUID> navigate;
    private boolean created;
    private boolean cancelled;
    private boolean completed;
}

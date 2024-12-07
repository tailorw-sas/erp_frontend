package com.kynsoft.finamer.creditcard.application.command.manageReconcileTransactionStatus.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateManageReconcileTransactionStatusRequest {

    private Status status;
    private String name;
    private String description;
    private Boolean requireValidation;
    private List<UUID> navigate;
    private boolean created;
    private boolean cancelled;
    private boolean completed;
}

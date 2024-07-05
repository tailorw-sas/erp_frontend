package com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.NavigateReconcileTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
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
}

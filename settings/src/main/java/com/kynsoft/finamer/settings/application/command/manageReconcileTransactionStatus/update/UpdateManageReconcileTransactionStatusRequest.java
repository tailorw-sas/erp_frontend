package com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.NavigateReconcileTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UpdateManageReconcileTransactionStatusRequest {

    private Status status;
    private String name;
    private String description;
    private Boolean requireValidation;
    private List<UUID> navigate;

}

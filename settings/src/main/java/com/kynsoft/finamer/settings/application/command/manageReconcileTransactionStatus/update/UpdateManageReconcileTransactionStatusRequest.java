package com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageReconcileTransactionStatusRequest {

    private Status status;
    private String name;
    private String description;

}

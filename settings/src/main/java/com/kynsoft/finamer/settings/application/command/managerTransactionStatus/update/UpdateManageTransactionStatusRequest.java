package com.kynsoft.finamer.settings.application.command.managerTransactionStatus.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.NavigateTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageTransactionStatusRequest {

    private String name;
    private String description;
    private Set<NavigateTransactionStatus> navigate;
    private Boolean enablePayment;
    private Boolean visible;
    private Status status;
}

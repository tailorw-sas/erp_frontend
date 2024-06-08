package com.kynsoft.finamer.settings.application.command.managerTransactionStatus.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Navigate;
import com.kynsoft.finamer.settings.domain.dtoEnum.NavigateTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageTransactionStatusRequest {
    private String code;
    private String name;
    private String description;
    private Set<NavigateTransactionStatus> navigate;
    private Boolean enablePayment;
    private Boolean visible;
    private Status status;
}

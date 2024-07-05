package com.kynsoft.finamer.settings.application.command.managerTransactionStatus.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageTransactionStatusRequest {
    private String code;
    private String name;
    private String description;
    private Set<UUID> navigate;
    private Boolean enablePayment;
    private Boolean visible;
    private Status status;
}

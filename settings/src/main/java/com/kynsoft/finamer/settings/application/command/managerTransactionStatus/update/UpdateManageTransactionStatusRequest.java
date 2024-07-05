package com.kynsoft.finamer.settings.application.command.managerTransactionStatus.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageTransactionStatusRequest {

    private String name;
    private String description;
    private List<UUID> navigate;
    private Boolean enablePayment;
    private Boolean visible;
    private Status status;
}

package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

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

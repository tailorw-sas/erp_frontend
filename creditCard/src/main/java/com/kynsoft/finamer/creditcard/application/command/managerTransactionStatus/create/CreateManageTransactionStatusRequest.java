package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.create;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

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

    private boolean sentStatus;
    private boolean refundStatus;
    private boolean receivedStatus;
}

package com.kynsoft.finamer.settings.application.command.managePaymentTransactionStatus.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.NavigatePaymentTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateManagePaymentTransactionStatusRequest {

    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean requireValidation;
    private List<UUID> navigate;
}

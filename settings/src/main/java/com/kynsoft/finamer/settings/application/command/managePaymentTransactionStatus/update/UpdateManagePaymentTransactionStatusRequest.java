package com.kynsoft.finamer.settings.application.command.managePaymentTransactionStatus.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateManagePaymentTransactionStatusRequest {

    private Status status;
    private String name;
    private String description;
    private Boolean requireValidation;
    private List<UUID> navigate;

}

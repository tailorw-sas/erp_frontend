package com.kynsoft.finamer.invoicing.application.command.manageInvoiceStatus.create;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateManageInvoiceStatusRequest {

    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean enabledToPrint;
    private Boolean enabledToPropagate;
    private Boolean enabledToApply;
    private Boolean enabledToPolicy;
    private Boolean processStatus;
    private List<UUID> navigate;

    private Boolean showClone;
    private boolean sentStatus;
    private boolean reconciledStatus;
    private boolean canceledStatus;
}

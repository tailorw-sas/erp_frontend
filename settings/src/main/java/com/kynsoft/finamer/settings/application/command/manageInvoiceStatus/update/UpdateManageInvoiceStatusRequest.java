package com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Navigate;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

@Getter
@Setter
public class UpdateManageInvoiceStatusRequest {

    private String description;
    private Status status;
    private String name;
    private Boolean enabledToPrint;
    private Boolean enabledToPropagate;
    private Boolean enabledToApply;
    private Boolean enabledToPolicy;
    private Boolean processStatus;
    private HashSet<Navigate> navigate;
}

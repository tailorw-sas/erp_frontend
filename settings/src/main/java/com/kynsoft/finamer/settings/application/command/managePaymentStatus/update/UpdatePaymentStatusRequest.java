package com.kynsoft.finamer.settings.application.command.managePaymentStatus.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePaymentStatusRequest {
    private String code;
    private String name;
    private Status status;
    private Boolean collected;
    private String description;
    private Boolean defaults;
    private Boolean applied;
}

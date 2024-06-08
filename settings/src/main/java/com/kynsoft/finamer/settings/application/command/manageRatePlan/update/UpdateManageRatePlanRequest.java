package com.kynsoft.finamer.settings.application.command.manageRatePlan.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageRatePlanRequest {
    private String name;
    private String hotel;
    private String description;
    private Status status;
}

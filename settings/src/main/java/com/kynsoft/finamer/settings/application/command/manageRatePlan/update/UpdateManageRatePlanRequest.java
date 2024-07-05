package com.kynsoft.finamer.settings.application.command.manageRatePlan.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageRatePlanRequest {
    private String name;
    private UUID hotel;
    private String description;
    private Status status;
}

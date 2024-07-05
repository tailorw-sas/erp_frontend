package com.kynsoft.finamer.settings.application.command.manageRatePlan.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageRatePlanRequest {
    private String code;
    private String name;
    private UUID hotel;
    private String description;
    private Status status;
}

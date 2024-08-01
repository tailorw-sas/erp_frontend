package com.kynsoft.finamer.settings.application.command.manageClient.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageClientRequest {
    private String code;
    private String name;
    private String description;
    private Status status;
    private Boolean isNightType;
}

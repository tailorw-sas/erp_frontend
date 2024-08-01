package com.kynsoft.finamer.settings.application.command.manageClient.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageClientRequest {
    private String name;
    private String description;
    private Status status;
    private Boolean isNightType;
}

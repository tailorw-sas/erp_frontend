package com.kynsoft.finamer.invoicing.application.command.manageClient.update;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageClientRequest {
    private String name;
    private String description;
    private Boolean isNightType;
}

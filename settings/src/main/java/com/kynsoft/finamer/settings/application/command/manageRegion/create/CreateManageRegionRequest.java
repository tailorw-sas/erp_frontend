package com.kynsoft.finamer.settings.application.command.manageRegion.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageRegionRequest {

    private String code;
    private String description;
    private Status status;
    private String name;
}

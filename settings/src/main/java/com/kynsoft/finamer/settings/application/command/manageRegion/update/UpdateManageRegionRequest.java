package com.kynsoft.finamer.settings.application.command.manageRegion.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageRegionRequest  {

    private String description;
    private Status status;
    private String name;
}

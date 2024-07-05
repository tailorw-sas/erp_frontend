package com.kynsoft.finamer.settings.application.command.manageReport.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageReportRequest  {

    private String description;
    private Status status;
    private String name;
    private String moduleId;
    private String moduleName;
}

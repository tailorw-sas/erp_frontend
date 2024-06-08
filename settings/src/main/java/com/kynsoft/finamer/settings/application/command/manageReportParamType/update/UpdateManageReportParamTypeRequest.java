package com.kynsoft.finamer.settings.application.command.manageReportParamType.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageReportParamTypeRequest {

    private Status status;
    private String name;
    private String label;
    private Boolean hotel;
    private String source;
}

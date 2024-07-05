package com.kynsoft.finamer.settings.application.command.manageReportParamType.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageReportParamTypeRequest {

    private Status status;
    private String name;
    private String label;
    private Boolean hotel;
    private String source;
    private String description;
}

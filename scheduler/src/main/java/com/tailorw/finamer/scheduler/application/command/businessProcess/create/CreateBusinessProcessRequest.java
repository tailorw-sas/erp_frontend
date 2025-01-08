package com.tailorw.finamer.scheduler.application.command.businessProcess.create;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBusinessProcessRequest {

    private String code;
    private String processName;
    private String description;
    private String status;
}

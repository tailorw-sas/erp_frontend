package com.tailorw.finamer.scheduler.application.command.businessProcess.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateBusinessProcessRequest {

    private String processName;
    private String description;
    private String status;
}

package com.kynsoft.finamer.settings.application.command.managerMessage.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerMessageRequest {

    private String code;
    private String description;
    private Status status;
    private String name;
    private String type;
    private UUID language;
}

package com.kynsoft.finamer.settings.application.command.managerMessage.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerMessageRequest {

    private String description;
    private Status status;
    private String name;
    private String type;
    private UUID language;
}

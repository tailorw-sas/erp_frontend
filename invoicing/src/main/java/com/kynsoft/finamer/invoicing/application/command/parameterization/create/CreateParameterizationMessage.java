package com.kynsoft.finamer.invoicing.application.command.parameterization.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateParameterizationMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_PARAMETERIZATION";

    public CreateParameterizationMessage(UUID id) {
        this.id = id;
    }
}

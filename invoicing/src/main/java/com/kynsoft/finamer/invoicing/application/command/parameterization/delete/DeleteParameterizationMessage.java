package com.kynsoft.finamer.invoicing.application.command.parameterization.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteParameterizationMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_PARAMETERIZATION";

    public DeleteParameterizationMessage(UUID id) {
        this.id = id;
    }
}

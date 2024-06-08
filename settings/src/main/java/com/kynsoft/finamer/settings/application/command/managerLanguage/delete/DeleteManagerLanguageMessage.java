package com.kynsoft.finamer.settings.application.command.managerLanguage.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManagerLanguageMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_LANGUAGE";

    public DeleteManagerLanguageMessage(UUID id) {
        this.id = id;
    }
}

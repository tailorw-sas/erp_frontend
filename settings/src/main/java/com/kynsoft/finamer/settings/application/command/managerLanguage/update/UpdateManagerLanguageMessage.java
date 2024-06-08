package com.kynsoft.finamer.settings.application.command.managerLanguage.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerLanguageMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGER_LANGUAGE";

    public UpdateManagerLanguageMessage(UUID id) {
        this.id = id;
    }
}

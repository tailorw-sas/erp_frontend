package com.kynsoft.finamer.settings.application.command.managerLanguage.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagerLanguageMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_LANGUAGE";

    public CreateManagerLanguageMessage(UUID id) {
        this.id = id;
    }
}

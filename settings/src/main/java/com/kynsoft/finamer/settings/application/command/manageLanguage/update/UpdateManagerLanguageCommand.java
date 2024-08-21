package com.kynsoft.finamer.settings.application.command.manageLanguage.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateManagerLanguageCommand implements ICommand {

    private UUID id;
    private String description;
    private Status status;
    private String name;
    private Boolean isEnabled;
    private Boolean defaults;

    public static UpdateManagerLanguageCommand fromRequest(UpdateManagerLanguageRequest request, UUID id){
        return new UpdateManagerLanguageCommand(
                id,
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.getIsEnabled(),
                request.getDefaults()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagerLanguageMessage(id);
    }
}

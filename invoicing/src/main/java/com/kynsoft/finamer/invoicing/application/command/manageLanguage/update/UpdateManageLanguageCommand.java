package com.kynsoft.finamer.invoicing.application.command.manageLanguage.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateManageLanguageCommand implements ICommand {

    private UUID id;
    private String name;
    private Boolean defaults;
    private String status;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageLanguageMessage(id);
    }
}

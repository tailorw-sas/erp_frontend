package com.kynsoft.finamer.creditcard.application.command.manageLanguage.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageLanguageCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private Boolean defaults;
    private String status;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageLanguageMessage(id);
    }
}

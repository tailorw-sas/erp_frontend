package com.kynsoft.finamer.payment.application.command.manageLanguage.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageLanguageCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private Boolean defaults;
    private String status;

    public CreateManageLanguageCommand(UUID id,
                                       String code,
                                       String name,
                                       Boolean defaults,
                                       String status){
        this.id = id;
        this.code = code;
        this.name = name;
        this.defaults = defaults;
        this.status = status;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageLanguageMessage(id);
    }
}

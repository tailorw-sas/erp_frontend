package com.kynsoft.finamer.invoicing.application.command.templateEntity.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateTemplateEntityCommand implements ICommand {
    private UUID id;
    private final String templateCode;
    private final String name;
    private String languageCode;
    private String type;

    @Override
    public ICommandMessage getMessage() {
        return new CreateTemplateEntityMessage(id);
    }
}

package com.kynsoft.finamer.payment.application.command.templateEntity.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateTemplateEntityCommand implements ICommand {
    private UUID id;
    private final String templateCode;
    private final String name;
    private String languageCode;
    private String type;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateTemplateEntityMessage();
    }
}

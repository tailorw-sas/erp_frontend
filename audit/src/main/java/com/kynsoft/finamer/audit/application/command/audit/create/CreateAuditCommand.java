package com.kynsoft.finamer.audit.application.command.audit.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateAuditCommand implements ICommand {

    private String entityName;

    private String username;

    private String action;

    private String data;

    private String tag;
    @Override
    public ICommandMessage getMessage() {
        return null;
    }
}

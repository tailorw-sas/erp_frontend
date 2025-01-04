package com.kynsoft.finamer.audit.application.command.audit.create;

import com.kynsoft.finamer.audit.domain.bus.command.ICommand;
import com.kynsoft.finamer.audit.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateAuditCommand implements ICommand {

    private String entityName;

    private String username;

    private String action;

    private String data;

    private String tag;

    private LocalDateTime time;

    private String serviceName;
    private UUID auditRegisterId;
    @Override
    public ICommandMessage getMessage() {
        return null;
    }
}

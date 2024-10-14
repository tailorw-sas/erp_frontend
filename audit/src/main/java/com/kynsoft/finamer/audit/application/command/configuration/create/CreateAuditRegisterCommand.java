package com.kynsoft.finamer.audit.application.command.configuration.create;

import com.kynsoft.finamer.audit.domain.bus.command.ICommand;
import com.kynsoft.finamer.audit.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateAuditRegisterCommand implements ICommand {

    private UUID auditRegisterId;
    private String serviceName;

    @Override
    public ICommandMessage getMessage() {
        return null;
    }
}

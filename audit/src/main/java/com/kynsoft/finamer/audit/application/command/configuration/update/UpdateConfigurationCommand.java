package com.kynsoft.finamer.audit.application.command.configuration.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateConfigurationCommand implements ICommand {
    private UUID id;
    private boolean auditUpdate;
    private boolean auditDelete;
    private boolean auditCreation;


    public UpdateConfigurationCommand(UpdateConfigurationRequest updateConfigurationRequest,UUID id) {
        this.id=id;
        this.auditCreation=updateConfigurationRequest.isAuditCreate();
        this.auditUpdate=updateConfigurationRequest.isAuditUpdate();
        this.auditDelete=updateConfigurationRequest.isAuditDelete();
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateConfigurationMessage(this.id);
    }
}

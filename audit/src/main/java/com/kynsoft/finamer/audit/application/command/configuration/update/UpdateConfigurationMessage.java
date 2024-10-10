package com.kynsoft.finamer.audit.application.command.configuration.update;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import java.util.UUID;

public record UpdateConfigurationMessage (UUID id, String command) implements ICommandMessage {
  private static final String DEFAULT_COMMAND="UPDATE_AUDIT_CONFIGURATION";
    public UpdateConfigurationMessage(UUID id){
        this(id,DEFAULT_COMMAND);
    }
}

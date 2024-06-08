package com.kynsof.identity.application.command.businessModule.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UpdateBusinessModuleCommand implements ICommand {

    private boolean result;
    private UUID idBusiness;
    private Set<UUID> modules;

    public UpdateBusinessModuleCommand(UUID idBusiness, Set<UUID> modules) {
        this.idBusiness = idBusiness;
        this.modules = Set.copyOf(modules);
    }

    public static UpdateBusinessModuleCommand fromRequest(UpdateBusinessModuleRequest request) {
        return new UpdateBusinessModuleCommand(
                request.getIdBusiness(), 
                request.getModules()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBusinessModuleMessage(result);
    }
}

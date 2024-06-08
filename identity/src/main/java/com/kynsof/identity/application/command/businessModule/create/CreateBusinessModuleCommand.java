package com.kynsof.identity.application.command.businessModule.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateBusinessModuleCommand implements ICommand {

    private boolean result;
    private UUID idBusiness;
    private List<UUID> modules;

    public CreateBusinessModuleCommand(UUID idBusiness, List<UUID> modules) {
        this.idBusiness = idBusiness;
        this.modules = List.copyOf(modules);
    }

    public static CreateBusinessModuleCommand fromRequest(CreateBusinessModuleRequest request) {
        return new CreateBusinessModuleCommand(
                request.getIdBusiness(), 
                request.getModules()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateBusinessModuleMessage(result);
    }
}

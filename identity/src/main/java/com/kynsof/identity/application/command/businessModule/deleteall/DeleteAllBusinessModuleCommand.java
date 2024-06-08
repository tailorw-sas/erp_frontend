package com.kynsof.identity.application.command.businessModule.deleteall;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeleteAllBusinessModuleCommand implements ICommand {

    private List<UUID> businessModules;

    public static DeleteAllBusinessModuleCommand fromRequest(DeleteAllBusinessModuleRequest request) {
        return new DeleteAllBusinessModuleCommand(
                request.getBusinessModules());
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteAllBusinessModuleMessage(true);
    }

}

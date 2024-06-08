package com.kynsoft.finamer.settings.application.command.manageAgencyType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageAgencyTypeCommand implements ICommand {

    private UUID id;
    private Status status;
    private String name;

    public static UpdateManageAgencyTypeCommand fromRequest(UpdateManageAgencyTypeRequest request, UUID id){
        return new UpdateManageAgencyTypeCommand(
                id, request.getStatus(), request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageAgencyTypeMessage(id);
    }
}

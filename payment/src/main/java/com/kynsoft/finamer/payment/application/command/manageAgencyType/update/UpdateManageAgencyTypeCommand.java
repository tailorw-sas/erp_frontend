package com.kynsoft.finamer.payment.application.command.manageAgencyType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageAgencyTypeCommand implements ICommand {

    private UUID id;
    private String name;
    private String status;    

    public static UpdateManageAgencyTypeCommand fromRequest(UpdateManageAgencyTypeRequest request, UUID id) {
        return new UpdateManageAgencyTypeCommand(
                id, request.getName(), request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageAgencyTypeMessage(id);
    }
}

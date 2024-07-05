package com.kynsoft.finamer.invoicing.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageAgencyCommand implements ICommand {

    private UUID id;
    private String name;
    

    public static UpdateManageAgencyCommand fromRequest(UpdateManageAgencyRequest request, UUID id) {
        return new UpdateManageAgencyCommand(
                id, request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageAgencyMessage(id);
    }
}

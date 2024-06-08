package com.kynsoft.finamer.settings.application.command.manageContact.update;

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
public class UpdateManageContactCommand implements ICommand {

    private UUID id;
    private String description;
    private Status status;
    private String name;
    private UUID manageHotel;
    private String email;
    private String phone;
    private Integer position;

    public static UpdateManageContactCommand fromRequest(UpdateManageContactRequest request, UUID id){
        return new UpdateManageContactCommand(
                id, request.getDescription(), request.getStatus(), request.getName(),
                request.getManageHotel(), request.getEmail(), request.getPhone(),
                request.getPosition()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageContactMessage(id);
    }
}

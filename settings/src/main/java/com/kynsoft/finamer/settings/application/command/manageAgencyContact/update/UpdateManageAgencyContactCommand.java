package com.kynsoft.finamer.settings.application.command.manageAgencyContact.update;

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
public class UpdateManageAgencyContactCommand implements ICommand {

    private UUID id;
    private UUID manageAgency;
    private UUID manageRegion;
    private List<UUID> manageHotel;
    private String emailContact;

    public static UpdateManageAgencyContactCommand fromRequest(UpdateManageAgencyContactRequest request, UUID id){
        return new UpdateManageAgencyContactCommand(
                id, request.getManageAgency(), request.getManageRegion(),
                request.getManageHotel(), request.getEmailContact()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageAgencyContactMessage(id);
    }
}

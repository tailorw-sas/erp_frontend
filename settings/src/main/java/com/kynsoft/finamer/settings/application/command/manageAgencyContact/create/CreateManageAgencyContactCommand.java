package com.kynsoft.finamer.settings.application.command.manageAgencyContact.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateManageAgencyContactCommand implements ICommand {

    private UUID id;
    private UUID manageAgency;
    private UUID manageRegion;
    private List<UUID> manageHotel;
    private String emailContact;

    public CreateManageAgencyContactCommand(UUID manageAgency, UUID manageRegion, List<UUID> manageHotel, String emailContact) {
        this.id = UUID.randomUUID();
        this.manageAgency = manageAgency;
        this.manageRegion = manageRegion;
        this.manageHotel = manageHotel;
        this.emailContact = emailContact;
    }

    public static CreateManageAgencyContactCommand fromRequest(CreateManageAgencyContactRequest request){
        return new CreateManageAgencyContactCommand(
                request.getManageAgency(), request.getManageRegion(), request.getManageHotel(), request.getEmailContact()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAgencyContactMessage(id);
    }
}

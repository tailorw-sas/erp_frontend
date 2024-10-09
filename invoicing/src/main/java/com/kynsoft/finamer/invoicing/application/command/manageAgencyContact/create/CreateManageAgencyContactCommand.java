package com.kynsoft.finamer.invoicing.application.command.manageAgencyContact.create;

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
public class CreateManageAgencyContactCommand implements ICommand {

    private UUID id;
    private UUID manageAgency;
    private UUID manageRegion;
    private List<UUID> manageHotel;
    private String emailContact;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAgencyContactMessage(id);
    }
}

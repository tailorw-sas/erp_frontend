package com.kynsoft.finamer.settings.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAgencyMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_AGENCY_TYPE";

    public CreateManageAgencyMessage(UUID id) {
        this.id = id;
    }
}

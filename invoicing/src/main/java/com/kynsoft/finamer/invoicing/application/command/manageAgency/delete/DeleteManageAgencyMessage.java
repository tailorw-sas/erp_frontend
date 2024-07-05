package com.kynsoft.finamer.invoicing.application.command.manageAgency.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageAgencyMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_AGENCY_TYPE";

    public DeleteManageAgencyMessage(UUID id) {
        this.id = id;
    }
}

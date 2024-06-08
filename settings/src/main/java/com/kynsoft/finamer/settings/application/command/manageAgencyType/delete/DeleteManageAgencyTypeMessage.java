package com.kynsoft.finamer.settings.application.command.manageAgencyType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageAgencyTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_AGENCY_TYPE";

    public DeleteManageAgencyTypeMessage(UUID id) {
        this.id = id;
    }
}

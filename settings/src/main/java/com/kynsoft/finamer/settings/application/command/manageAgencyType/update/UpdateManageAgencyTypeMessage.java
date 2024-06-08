package com.kynsoft.finamer.settings.application.command.manageAgencyType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageAgencyTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_AGENCY_TYPE";

    public UpdateManageAgencyTypeMessage(UUID id) {
        this.id = id;
    }
}

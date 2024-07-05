package com.kynsoft.finamer.payment.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageAgencyMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_AGENCY_TYPE";

    public UpdateManageAgencyMessage(UUID id) {
        this.id = id;
    }
}

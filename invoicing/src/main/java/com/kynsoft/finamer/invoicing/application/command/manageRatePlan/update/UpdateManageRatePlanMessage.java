package com.kynsoft.finamer.invoicing.application.command.manageRatePlan.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageRatePlanMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_RATE_PLAN";

    public UpdateManageRatePlanMessage(UUID id) {
        this.id = id;
    }

}

package com.kynsoft.finamer.invoicing.application.command.manageRatePlan.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageRatePlanMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_RATE_PLAN";

    public DeleteManageRatePlanMessage(UUID id) {
        this.id = id;
    }

}

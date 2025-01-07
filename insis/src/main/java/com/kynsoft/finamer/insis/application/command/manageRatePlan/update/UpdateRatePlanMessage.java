package com.kynsoft.finamer.insis.application.command.manageRatePlan.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import java.util.UUID;

public class UpdateRatePlanMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "UPDATE_MANAGE_RATE_PLAN";

    public UpdateRatePlanMessage(UUID id){
        this.id = id;
    }
}

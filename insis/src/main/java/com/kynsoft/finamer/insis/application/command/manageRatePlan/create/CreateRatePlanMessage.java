package com.kynsoft.finamer.insis.application.command.manageRatePlan.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Setter;

import java.util.UUID;

@Setter
public class CreateRatePlanMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "CREATE_MANAGE_RATE_PLAN";

    public CreateRatePlanMessage(UUID id){
        this.id = id;
    }
}

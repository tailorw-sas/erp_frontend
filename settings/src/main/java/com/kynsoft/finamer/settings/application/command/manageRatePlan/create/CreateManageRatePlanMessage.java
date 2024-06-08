package com.kynsoft.finamer.settings.application.command.manageRatePlan.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageRatePlanMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_RATE_PLAN";

    public CreateManageRatePlanMessage(UUID id) {
        this.id = id;
    }

}

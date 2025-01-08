package com.kynsoft.finamer.insis.application.command.manageRatePlan.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateManyManageRatePlanMessage implements ICommandMessage {
    private final String command = "CREATE_MANY_RATE_PLAN_COMMAND";

    public CreateManyManageRatePlanMessage(){}
}

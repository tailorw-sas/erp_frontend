package com.kynsoft.finamer.insis.application.command.manageAgency.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateManyManageAgencyMessage implements ICommandMessage {
    private final String command = "CREATE_MANY_MANAGE_AGENCY_COMMAND";

    public CreateManyManageAgencyMessage(){}
}

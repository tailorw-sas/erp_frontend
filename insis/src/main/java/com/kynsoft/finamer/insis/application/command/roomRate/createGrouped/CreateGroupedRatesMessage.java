package com.kynsoft.finamer.insis.application.command.roomRate.createGrouped;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateGroupedRatesMessage implements ICommandMessage {
    private final String command = "CREATE_GROUPED_RATES_COMMAND";

    public CreateGroupedRatesMessage(){}
}

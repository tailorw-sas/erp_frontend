package com.kynsoft.finamer.settings.application.command.manageReportParamType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageReportParamTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_REPORT_PARAM_TYPE";

    public CreateManageReportParamTypeMessage(UUID id) {
        this.id = id;
    }
}

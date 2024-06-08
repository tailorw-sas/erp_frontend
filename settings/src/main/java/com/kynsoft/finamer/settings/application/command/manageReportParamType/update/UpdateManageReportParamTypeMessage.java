package com.kynsoft.finamer.settings.application.command.manageReportParamType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageReportParamTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_REPORT_PARAM_TYPE";

    public UpdateManageReportParamTypeMessage(UUID id) {
        this.id = id;
    }
}

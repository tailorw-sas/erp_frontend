package com.kynsoft.finamer.settings.application.command.manageReportParamType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageReportParamTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_REPORT_PARAM_TYPE";

    public DeleteManageReportParamTypeMessage(UUID id) {
        this.id = id;
    }
}

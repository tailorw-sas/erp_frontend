package com.kynsoft.finamer.settings.application.command.manageReport.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageReportMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "DELETE_MANAGE_REPORT";

    public DeleteManageReportMessage(UUID id) {
        this.id = id;
    }
}

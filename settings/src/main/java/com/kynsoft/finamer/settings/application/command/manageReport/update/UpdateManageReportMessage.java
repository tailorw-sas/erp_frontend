package com.kynsoft.finamer.settings.application.command.manageReport.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageReportMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_REPORT";

    public UpdateManageReportMessage(UUID id) {
        this.id = id;
    }
}

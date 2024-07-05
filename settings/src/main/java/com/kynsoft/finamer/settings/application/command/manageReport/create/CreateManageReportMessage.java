package com.kynsoft.finamer.settings.application.command.manageReport.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageReportMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_REPORT";

    public CreateManageReportMessage(UUID id) {
        this.id = id;
    }

}

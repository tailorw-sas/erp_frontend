package com.kynsoft.report.applications.command.jasperReportParameter.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateJasperReportParameterMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "UPDATE_REPORT_PARAMETER";

    public UpdateJasperReportParameterMessage(UUID id) {
        this.id = id;
    }
}

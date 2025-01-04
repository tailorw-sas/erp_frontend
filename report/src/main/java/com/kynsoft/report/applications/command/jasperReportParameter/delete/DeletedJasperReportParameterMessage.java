package com.kynsoft.report.applications.command.jasperReportParameter.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeletedJasperReportParameterMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "DELETE_REPORT_PARAMETER";

    public DeletedJasperReportParameterMessage(UUID id) {
        this.id = id;
    }
}

package com.kynsoft.report.applications.command.jasperReportParameter.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateJasperReportParameterMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "CREATE_REPORT_PARAMETER";


    public CreateJasperReportParameterMessage(UUID id) {
        this.id = id;
    }
}

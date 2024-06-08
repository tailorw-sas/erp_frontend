package com.kynsoft.report.applications.command.jasperReportTemplate.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateJasperReportTemplateMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_JASPER_REPORT_TEMPLATE";

    public UpdateJasperReportTemplateMessage(UUID id) {
        this.id = id;
    }

}

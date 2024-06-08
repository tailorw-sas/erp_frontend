package com.kynsoft.report.applications.command.jasperReportTemplate.delete;



import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteJasperReportTemplateMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_JASPER_REPORT_TEMPLATE";

    public DeleteJasperReportTemplateMessage(UUID id) {
        this.id = id;
    }

}

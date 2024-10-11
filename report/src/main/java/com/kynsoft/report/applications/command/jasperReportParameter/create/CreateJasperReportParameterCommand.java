package com.kynsoft.report.applications.command.jasperReportParameter.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateJasperReportParameterCommand implements ICommand {

    private UUID id;
    private final String paramName;
    private final String type;
    private final String module;
    private final String service;
    private final String label;
    private final UUID reportId;

    public CreateJasperReportParameterCommand(String paramName, String type, String module, String service, String label, UUID reportId) {
        this.paramName = paramName;
        this.type = type;
        this.module = module;
        this.service = service;
        this.label = label;
        this.reportId = reportId;

        this.id = UUID.randomUUID();
    }

    public static CreateJasperReportParameterCommand fromRequest(CreateJasperReportParameterRequest request){
        return new CreateJasperReportParameterCommand(
                request.getParamName(), request.getType(), request.getModule(),
                request.getService(), request.getLabel(), request.getReportId() );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateJasperReportParameterMessage(id);
    }
}

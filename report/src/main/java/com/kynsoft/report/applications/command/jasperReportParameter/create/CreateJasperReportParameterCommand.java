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
    private final String componentType;
    private final String reportClass;
    private final String reportValidation;

    public CreateJasperReportParameterCommand(String paramName, String type, String module, String service, String label,
                                              UUID reportId, String componentType, String reportClass, String reportValidation) {
        this.paramName = paramName;
        this.type = type;
        this.module = module;
        this.service = service;
        this.label = label;
        this.reportId = reportId;
        this.componentType = componentType;
        this.reportClass = reportClass;
        this.reportValidation = reportValidation;

        this.id = UUID.randomUUID();
    }

    public static CreateJasperReportParameterCommand fromRequest(CreateJasperReportParameterRequest request){
        return new CreateJasperReportParameterCommand(
                request.getParamName(), request.getType(), request.getModule(),
                request.getService(), request.getLabel(), request.getReportId(), request.getComponentType(),
                request.getReportClass(), request.getReportValidation() );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateJasperReportParameterMessage(id);
    }
}

package com.kynsoft.report.applications.command.jasperReportTemplate.update;

import com.kynsoft.report.domain.dto.JasperReportTemplateType;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateJasperReportTemplateCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private JasperReportTemplateType type;
    private String file;
    private String parameters;

    public UpdateJasperReportTemplateCommand(UUID id, String code, String name, String description,
                                             JasperReportTemplateType type, String file, String parameters) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.type = type;
        this.file = file;
        this.parameters = parameters;
    }

    public static UpdateJasperReportTemplateCommand fromRequest(UpdateJasperReportTemplateRequest request, UUID id) {
        return new UpdateJasperReportTemplateCommand(
                id,
                request.getCode(), 
                request.getName(), 
                request.getDescription(), 
                request.getType(), 
                request.getFile(),
                request.getParameters()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateJasperReportTemplateMessage(id);
    }
}

package com.kynsoft.report.applications.command.jasperReportTemplate.create;

import com.kynsoft.report.domain.dto.JasperReportTemplateType;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.domain.dto.status.ModuleSystems;
import com.kynsoft.report.domain.dto.status.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateJasperReportTemplateCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private JasperReportTemplateType type;
    private Status status;
    private String file;

    private Double menuPosition;
    private UUID dbConection;
    private String query;

    private final ModuleSystems moduleSystems;

    public CreateJasperReportTemplateCommand(String code, String name, String description, JasperReportTemplateType type,
                                             String file,
                                             Double menuPosition,
                                           UUID dbConection,
                                              ModuleSystems moduleSystems) {
        this.moduleSystems = moduleSystems;

        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.description = description;
        this.type = type;
        this.file = file;
        this.menuPosition = menuPosition;
        this.dbConection = dbConection;
    }

    public static CreateJasperReportTemplateCommand fromRequest(CreateJasperReportTemplateRequest request) {
        return new CreateJasperReportTemplateCommand(
                request.getCode(), 
                request.getName(), 
                request.getDescription(), 
                request.getType(), 
                request.getFile(),
                request.getMenuPosition(),
                request.getDbConection(),
                request.getModuleSystems()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateJasperReportTemplateMessage(id);
    }
}

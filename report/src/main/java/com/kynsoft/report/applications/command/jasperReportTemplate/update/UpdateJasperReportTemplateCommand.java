package com.kynsoft.report.applications.command.jasperReportTemplate.update;

import com.kynsoft.report.domain.dto.JasperReportTemplateType;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.domain.dto.status.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateJasperReportTemplateCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private JasperReportTemplateType type;
    private String file;
    private String parameters;

    private Double parentIndex;
    private Double menuPosition;
    private String lanPath;
    private Boolean web;
    private Boolean subMenu;
    private Boolean sendEmail;
    private Boolean internal;
    private Boolean highRisk;
    private Boolean visible;
    private Boolean cancel;
    private String rootIndex;
    private String language;
    private Status status;
    private UUID dbConection;
    private String query;

    public UpdateJasperReportTemplateCommand(UUID id, String name, String description,
                                             JasperReportTemplateType type, String file, String parameters,
                                             Double parentIndex, Double menuPosition, 
                                             String lanPath, Boolean web, Boolean subMenu, Boolean sendEmail, 
                                             Boolean internal, Boolean highRisk, Boolean visible, Boolean cancel, 
                                             String rootIndex, String language, Status status, UUID dbConection,
                                             String query) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.file = file;
        this.parameters = parameters;
        this.parentIndex = parentIndex;
        this.menuPosition = menuPosition;
        this.lanPath = lanPath;
        this.web = web;
        this.subMenu = subMenu;
        this.sendEmail = sendEmail;
        this.internal = internal;
        this.highRisk = highRisk;
        this.visible = visible;
        this.cancel = cancel;
        this.rootIndex = rootIndex;
        this.language = language;
        this.status = status;
        this.dbConection = dbConection;
        this.query = query;
    }

    public static UpdateJasperReportTemplateCommand fromRequest(UpdateJasperReportTemplateRequest request, UUID id) {
        return new UpdateJasperReportTemplateCommand(
                id,
                request.getName(), 
                request.getDescription(), 
                request.getType(), 
                request.getFile(),
                request.getParameters(),
                request.getParentIndex(),
                request.getMenuPosition(),
                request.getLanPath(),
                request.getWeb(),
                request.getSubMenu(),
                request.getSendEmail(),
                request.getInternal(),
                request.getHighRisk(),
                request.getVisible(),
                request.getCancel(),
                request.getRootIndex(),
                request.getLanguage(),
                request.getStatus(),
                request.getDbConection(),
                request.getQuery()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateJasperReportTemplateMessage(id);
    }
}

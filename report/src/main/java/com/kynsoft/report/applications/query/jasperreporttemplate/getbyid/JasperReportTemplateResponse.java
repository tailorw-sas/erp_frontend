package com.kynsoft.report.applications.query.jasperreporttemplate.getbyid;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.applications.query.dbconection.getById.DBConectionResponse;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateType;
import com.kynsoft.report.domain.dto.status.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JasperReportTemplateResponse implements IResponse {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String file;
    private JasperReportTemplateType type;
    private Status status;
    private String parameters;
    private LocalDate createdAt;

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
    private DBConectionResponse dbConection;
    private String query;

    public JasperReportTemplateResponse(JasperReportTemplateDto jasperReportTemplateDto) {
        this.id = jasperReportTemplateDto.getId();
        this.code = jasperReportTemplateDto.getCode();
        this.name = jasperReportTemplateDto.getName();
        this.description = jasperReportTemplateDto.getDescription();
        this.file = jasperReportTemplateDto.getFile();
        this.type = jasperReportTemplateDto.getType();
        this.parameters = jasperReportTemplateDto.getParameters();
        this.createdAt = jasperReportTemplateDto.getCreatedAt().toLocalDate();
        
        this.parentIndex = jasperReportTemplateDto.getParentIndex();
        this.menuPosition = jasperReportTemplateDto.getMenuPosition();
        this.lanPath = jasperReportTemplateDto.getLanPath();
        this.web = jasperReportTemplateDto.getWeb();
        this.subMenu = jasperReportTemplateDto.getSubMenu();
        this.sendEmail = jasperReportTemplateDto.getSendEmail();
        this.internal = jasperReportTemplateDto.getInternal();
        this.highRisk = jasperReportTemplateDto.getHighRisk();
        this.visible = jasperReportTemplateDto.getVisible();
        this.cancel = jasperReportTemplateDto.getCancel();
        this.rootIndex = jasperReportTemplateDto.getRootIndex();
        this.language = jasperReportTemplateDto.getLanguage();
        this.status = jasperReportTemplateDto.getStatus();
        this.dbConection = jasperReportTemplateDto.getDbConection() != null ? new DBConectionResponse(jasperReportTemplateDto.getDbConection()) : null;
        this.query = jasperReportTemplateDto.getQuery();
    }

}

package com.kynsoft.report.domain.dto;

import com.kynsoft.report.domain.dto.status.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JasperReportTemplateDto {
    private  UUID id;
    private  String code;
    private  String name;
    private  String description;
    private  String file;
    private  JasperReportTemplateType type;
    private Status status;
    private  String parameters;
    private LocalDateTime createdAt;

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

    private DBConectionDto dbConection;

    private String query;

    public JasperReportTemplateDto(UUID id, String templateCode, String templateName, 
                                   String templateDescription, String templateContentUrl, 
                                   JasperReportTemplateType type, String parameters, 
                                   Double parentIndex, Double menuPosition, 
                                   String lanPath, Boolean web, Boolean subMenu, Boolean sendEmail, 
                                   Boolean internal, Boolean highRisk, Boolean visible, Boolean cancel, 
                                   String rootIndex, String language, Status status, DBConectionDto dbConection,
                                   String query) {
        this.id = id;
        this.code = templateCode;
        this.name = templateName;
        this.description = templateDescription;
        this.file = templateContentUrl;
        this.type = type;
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

}

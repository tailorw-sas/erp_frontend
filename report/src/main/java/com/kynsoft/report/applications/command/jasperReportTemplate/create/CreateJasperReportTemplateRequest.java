package com.kynsoft.report.applications.command.jasperReportTemplate.create;

import com.kynsoft.report.domain.dto.JasperReportTemplateType;
import com.kynsoft.report.domain.dto.status.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateJasperReportTemplateRequest {
    private String code;
    private String name;
    private String description;
    private JasperReportTemplateType type;
    private Status status;
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
    private UUID dbConection;
    private String query;
}

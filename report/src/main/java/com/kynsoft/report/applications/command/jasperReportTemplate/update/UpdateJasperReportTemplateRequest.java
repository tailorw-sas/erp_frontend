package com.kynsoft.report.applications.command.jasperReportTemplate.update;

import com.kynsoft.report.domain.dto.JasperReportTemplateType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateJasperReportTemplateRequest {
    private String code;
    private String name;
    private String description;
    private JasperReportTemplateType type;
    private String file;
    private String parameters;
}

package com.kynsoft.report.applications.command.jasperReportTemplate.create;

import com.kynsoft.report.domain.dto.JasperReportTemplateType;
import com.kynsoft.report.domain.dto.status.ModuleSystems;
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
    private String file;
    private Double menuPosition;
    private UUID dbConection;
    private ModuleSystems moduleSystems;
}

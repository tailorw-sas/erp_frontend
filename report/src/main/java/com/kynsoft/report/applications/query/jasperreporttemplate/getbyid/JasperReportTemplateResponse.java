package com.kynsoft.report.applications.query.jasperreporttemplate.getbyid;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateType;
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
    private String url;
    private JasperReportTemplateType type;
    private String parameters;
    private LocalDate createdAt;


    public JasperReportTemplateResponse(JasperReportTemplateDto jasperReportTemplateDto) {
        this.id = jasperReportTemplateDto.getId();
        this.code = jasperReportTemplateDto.getTemplateCode();
        this.name = jasperReportTemplateDto.getTemplateName();
        this.description = jasperReportTemplateDto.getTemplateDescription();
        this.url = jasperReportTemplateDto.getTemplateContentUrl();
        this.type = jasperReportTemplateDto.getType();
        this.parameters = jasperReportTemplateDto.getParameters();
        this.createdAt = jasperReportTemplateDto.getCreatedAt().toLocalDate();
    }

}

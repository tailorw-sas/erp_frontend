package com.kynsoft.report.domain.dto;

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
    private  String templateCode;
    private  String templateName;
    private  String templateDescription;
    private  String templateContentUrl;
    private  JasperReportTemplateType type;
    private  String parameters;
    private LocalDateTime createdAt;

    public JasperReportTemplateDto(UUID id, String templateCode, String templateName, String templateDescription,
                                   String templateContentUrl, JasperReportTemplateType type, String parameters) {
        this.id = id;
        this.templateCode = templateCode;
        this.templateName = templateName;
        this.templateDescription = templateDescription;
        this.templateContentUrl = templateContentUrl;
        this.type = type;
        this.parameters = parameters;
    }
}

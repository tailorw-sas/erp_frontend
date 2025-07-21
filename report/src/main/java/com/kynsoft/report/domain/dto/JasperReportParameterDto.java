package com.kynsoft.report.domain.dto;

import com.kynsoft.report.infrastructure.enums.JasperParameterCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JasperReportParameterDto {

    private UUID id;
    private String paramName;
    private String type;
    private String module;
    private String service;
    private String label;
    private String componentType;
    private JasperReportTemplateDto jasperReportTemplate;
    private int parameterPosition;
    private String dependentField;
    private String filterKeyValue;
    private String dataValueStatic;
    /**
     * Category of the parameter: REPORT or COMPLEMENTARY
     */
    private JasperParameterCategory parameterCategory;
}
package com.kynsoft.report.applications.command.jasperReportParameter.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateJasperReportParameterRequest {

    private  String paramName;
    private  String type;
    private  String module;
    private  String service;
    private  String label;
    private String componentType;
    private  UUID reportId;
    private int parameterPosition;
    private String dependentField;
    private String filterKeyValue;
    private String dataValueStatic;
    private String parameterCategory;
}

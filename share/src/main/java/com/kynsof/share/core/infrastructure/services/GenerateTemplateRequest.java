package com.kynsof.share.core.infrastructure.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTemplateRequest {
    private Map<String, Object> parameters;
    private String JasperReportCode;
}

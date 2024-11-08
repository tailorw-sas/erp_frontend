package com.kynsoft.report.controller;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ReportRequest {
    private String templateUrl;
    private Map<String, Object> parameters;
    private List<Map<String, Object>> data;
}

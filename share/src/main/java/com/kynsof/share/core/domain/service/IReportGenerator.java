package com.kynsof.share.core.domain.service;

import java.util.Map;

public interface IReportGenerator {
    byte[] generateReport(Map<String, Object> parameters, String jasperReportCode);
}

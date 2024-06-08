package com.kynsoft.report.domain.services;

import net.sf.jasperreports.engine.JREmptyDataSource;

import java.util.Map;

public interface IReportService {
    byte[] generatePdfReport(Map<String, Object> parameters, String jrxmlUrl, JREmptyDataSource jrEmptyDataSource);

    String getReportParameters(String jrxmlUrl);
}

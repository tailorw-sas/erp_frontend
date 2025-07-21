package com.kynsoft.report.applications.command.generateTemplate;

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
        private String jasperReportCode;
        private String reportFormatType;
        private Map<String, Object> parameters;
        private String requestId;
        private Metadata metadata;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Metadata {
                private String timestamp;
                private String userAgent;
        }
}
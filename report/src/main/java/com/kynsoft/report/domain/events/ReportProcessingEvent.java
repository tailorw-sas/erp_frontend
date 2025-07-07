package com.kynsoft.report.domain.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportProcessingEvent {
    @JsonProperty("serverRequestId")
    private String serverRequestId;

    @JsonProperty("clientRequestId")
    private String clientRequestId;

    @JsonProperty("jasperReportCode")
    private String jasperReportCode;

    @JsonProperty("reportFormatType")
    private String reportFormatType;

    @JsonProperty("parameters")
    private Map<String, Object> parameters;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("originalRequestJson")
    private String originalRequestJson;
}

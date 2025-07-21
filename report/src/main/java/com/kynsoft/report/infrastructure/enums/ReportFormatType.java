package com.kynsoft.report.infrastructure.enums;

public enum ReportFormatType {
    PDF("application/pdf"),
    XLS("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    CSV("text/csv");

    private final String mediaType;

    ReportFormatType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public static ReportFormatType fromString(String value) {
        for (ReportFormatType type : ReportFormatType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported report format type: " + value);
    }
}

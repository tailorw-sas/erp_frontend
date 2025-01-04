package com.kynsoft.finamer.audit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class AuditRecordDto {
    private UUID auditRegisterId;

    private String entityName;

    private String username;

    private String action;

    private String data;

    private String tag;

    private LocalDateTime localDateTime;

    private String serviceName;
}

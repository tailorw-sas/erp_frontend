package com.kynsoft.finamer.audit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuditRecordDto {

    private String entityName;

    private String username;

    private String action;

    private String data;

    private String tag;
}

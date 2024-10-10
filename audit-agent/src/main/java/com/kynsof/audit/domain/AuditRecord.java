package com.kynsof.audit.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuditRecord {
    private String entityName;

    private String username;

    private String action;

    private String data;
}

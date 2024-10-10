package com.kynsof.audit.domain.kafka;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuditKafka {

    private String entityName;

    private String username;

    private String action;

    private String data;
}

package com.kynsof.audit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuditRegisterDto {
    private String entityName;
}

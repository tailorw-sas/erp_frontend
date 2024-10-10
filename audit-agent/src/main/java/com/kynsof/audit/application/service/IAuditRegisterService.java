package com.kynsof.audit.application.service;


import com.kynsof.audit.domain.dto.AuditRegisterDto;
import com.kynsof.audit.domain.dto.AuditRegisterRecordDto;

public interface IAuditRegisterService {

    public void registerEntity(AuditRegisterDto auditRegisterDto);

    public AuditRegisterRecordDto getAuditRegisterService(String entityName);
}

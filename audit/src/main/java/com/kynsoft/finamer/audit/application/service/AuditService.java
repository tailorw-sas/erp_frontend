package com.kynsoft.finamer.audit.application.service;


import com.kynsoft.finamer.audit.domain.dto.AuditRecordDto;
import com.kynsoft.finamer.audit.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

public interface AuditService {

    public void create(AuditRecordDto auditRecordDto);

    public PaginatedResponse findAll(Pageable pageable);
}

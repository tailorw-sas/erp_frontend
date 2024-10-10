package com.kynsoft.finamer.audit.application.service;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.audit.domain.dto.AuditRecordDto;
import org.springframework.data.domain.Pageable;

public interface AuditService {

    public void create(AuditRecordDto auditRecordDto);

    public PaginatedResponse findAll(Pageable pageable);
}

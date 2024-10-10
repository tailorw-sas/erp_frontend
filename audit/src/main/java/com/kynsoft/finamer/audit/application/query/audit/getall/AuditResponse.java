package com.kynsoft.finamer.audit.application.query.audit.getall;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.audit.domain.dto.AuditRecordDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuditResponse implements IResponse {
    private AuditRecordDto auditRecordDto;
}

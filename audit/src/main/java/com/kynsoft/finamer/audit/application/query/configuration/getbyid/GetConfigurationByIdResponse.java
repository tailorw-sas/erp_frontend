package com.kynsoft.finamer.audit.application.query.configuration.getbyid;


import com.kynsoft.finamer.audit.domain.bus.query.IResponse;
import com.kynsoft.finamer.audit.domain.dto.AuditConfigurationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetConfigurationByIdResponse implements IResponse {

    private final AuditConfigurationDto auditConfigurationDto;

}

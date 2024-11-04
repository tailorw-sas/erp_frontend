package com.kynsoft.finamer.audit.application.command.configuration.update;

import com.kynsoft.finamer.audit.application.service.AuditConfigurationService;
import com.kynsoft.finamer.audit.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.audit.domain.dto.AuditConfigurationDto;
import org.springframework.stereotype.Component;

@Component
public class UpdateConfigurationCommandHandler implements ICommandHandler<UpdateConfigurationCommand> {
    private final AuditConfigurationService auditConfigurationService;

    public UpdateConfigurationCommandHandler(AuditConfigurationService auditConfigurationService) {
        this.auditConfigurationService = auditConfigurationService;
    }

    @Override
    public void handle(UpdateConfigurationCommand command) {
        AuditConfigurationDto auditConfigurationDto = new AuditConfigurationDto(command.getId(),command.isAuditCreation(),
                command.isAuditUpdate(), command.isAuditDelete(), command.getServiceName(), command.getEntityName());
        auditConfigurationService.update(auditConfigurationDto);
    }
}

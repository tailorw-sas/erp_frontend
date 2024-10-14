package com.kynsoft.finamer.audit.application.command.configuration.create;

import com.kynsoft.finamer.audit.application.service.AuditConfigurationService;
import com.kynsoft.finamer.audit.application.service.AuditService;
import com.kynsoft.finamer.audit.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.audit.domain.dto.AuditConfigurationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class CreateAuditRegisterCommandHandler implements ICommandHandler<CreateAuditRegisterCommand> {
    private final Logger logger= LoggerFactory.getLogger(CreateAuditRegisterCommandHandler.class);
    private final AuditConfigurationService configurationService;

    public CreateAuditRegisterCommandHandler( AuditConfigurationService configurationService) {
        this.configurationService = configurationService;

    }

    @Override
    public void handle(CreateAuditRegisterCommand command) {
        AuditConfigurationDto auditConfiguration = new AuditConfigurationDto(command.getAuditRegisterId(),true,true,true,command.getServiceName());
        logger.info("Recibiendo evento de registro {}:",auditConfiguration);
        Optional<AuditConfigurationDto> configuration = configurationService.findByServiceNameAndRegisterId(command.getServiceName(), command.getAuditRegisterId());
        if (configuration.isEmpty()){
            configurationService.registerServiceAudit(auditConfiguration);
            logger.info("Registrado el servicio");
        }

    }
}

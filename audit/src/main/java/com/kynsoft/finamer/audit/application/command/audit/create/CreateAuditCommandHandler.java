package com.kynsoft.finamer.audit.application.command.audit.create;

import com.kynsoft.finamer.audit.application.service.AuditConfigurationService;
import com.kynsoft.finamer.audit.application.service.AuditService;
import com.kynsoft.finamer.audit.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.audit.domain.dto.AuditConfigurationDto;
import com.kynsoft.finamer.audit.domain.dto.AuditRecordDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;


@Component
public class CreateAuditCommandHandler implements ICommandHandler<CreateAuditCommand> {
    private final Logger logger= LoggerFactory.getLogger(CreateAuditCommandHandler.class);
    private final AuditService service;
    private final AuditConfigurationService configurationService;

    public CreateAuditCommandHandler(AuditService service, AuditConfigurationService configurationService) {
        this.service = service;
        this.configurationService = configurationService;

    }

    @Override
    public void handle(CreateAuditCommand command) {
        AuditRecordDto auditRecordDto = new AuditRecordDto(command.getAuditRegisterId(),command.getEntityName(), command.getUsername(),
                command.getAction(), command.getData(), command.getTag(), command.getTime(), command.getServiceName());
        logger.info("Recibiendo evento de auditoria {}:",auditRecordDto);
        Assert.notNull(auditRecordDto.getServiceName(), "El nombre del servicio es desconocido");
        Optional<AuditConfigurationDto> configuration = configurationService.findByServiceNameAndRegisterId(auditRecordDto.getServiceName(), auditRecordDto.getAuditRegisterId());
        if (configuration.isPresent()){
            service.create(auditRecordDto);
        }else{
            logger.error("No existe configuracion registrada para este evento {}:",auditRecordDto);
        }
    }
}

package com.kynsoft.finamer.audit.application.command.audit.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.audit.application.service.AuditService;
import com.kynsoft.finamer.audit.domain.dto.AuditRecordDto;
import org.springframework.stereotype.Component;

@Component
public class CreateAuditCommandHandler implements ICommandHandler<CreateAuditCommand> {

    private final AuditService service;

    public CreateAuditCommandHandler(AuditService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateAuditCommand command) {
        AuditRecordDto auditRecordDto = new AuditRecordDto(command.getEntityName(), command.getUsername(), command.getAction(), command.getData(),command.getTag());
        service.create(auditRecordDto);
    }
}

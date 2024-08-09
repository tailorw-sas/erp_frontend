package com.kynsoft.report.applications.command.dbconection.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.services.IDBConectionService;
import org.springframework.stereotype.Component;

@Component
public class CreateDBConectionCommandHandler implements ICommandHandler<CreateDBConectionCommand> {

    private final IDBConectionService service;

    public CreateDBConectionCommandHandler(IDBConectionService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateDBConectionCommand command) {
        this.service.create(new DBConectionDto(
                command.getId(), command.getUrl(), command.getUsername(), command.getPassword()
        ));
    }
}

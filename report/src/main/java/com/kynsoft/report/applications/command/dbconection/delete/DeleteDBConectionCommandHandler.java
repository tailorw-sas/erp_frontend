package com.kynsoft.report.applications.command.dbconection.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.services.IDBConnectionService;
import org.springframework.stereotype.Component;

@Component
public class DeleteDBConectionCommandHandler implements ICommandHandler<DeleteDBConectionCommand> {

    private final IDBConnectionService service;

    public DeleteDBConectionCommandHandler(IDBConnectionService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteDBConectionCommand command) {
        DBConectionDto dto = this.service.findById(command.getId());
        this.service.delete(dto);
    }
}

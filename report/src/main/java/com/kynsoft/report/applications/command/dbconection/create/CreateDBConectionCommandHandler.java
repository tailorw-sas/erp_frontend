package com.kynsoft.report.applications.command.dbconection.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.rules.dbconection.DBConectionCodeMustBeUniqueRule;
import com.kynsoft.report.domain.services.IDBConnectionService;
import org.springframework.stereotype.Component;

@Component
public class CreateDBConectionCommandHandler implements ICommandHandler<CreateDBConectionCommand> {

    private final IDBConnectionService service;

    public CreateDBConectionCommandHandler(IDBConnectionService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateDBConectionCommand command) {
        RulesChecker.checkRule(new DBConectionCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));
        this.service.create(new DBConectionDto(
                command.getId(), command.getUrl(), command.getUsername(), command.getPassword(),
                command.getCode(), command.getName(), command.getStatus()
        ));
    }
}

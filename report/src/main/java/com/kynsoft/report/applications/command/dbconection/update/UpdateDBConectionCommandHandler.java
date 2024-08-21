package com.kynsoft.report.applications.command.dbconection.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.rules.dbconection.DBConectionCodeMustBeUniqueRule;
import com.kynsoft.report.domain.services.IDBConectionService;
import org.springframework.stereotype.Component;

@Component
public class UpdateDBConectionCommandHandler implements ICommandHandler<UpdateDBConectionCommand> {

    private final IDBConectionService service;

    public UpdateDBConectionCommandHandler(IDBConectionService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateDBConectionCommand command) {
        RulesChecker.checkRule(new DBConectionCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));
        DBConectionDto dto = this.service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setUrl, command.getUrl(), dto.getUrl(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setUsername, command.getUsername(), dto.getUsername(), update::setUpdate);
//        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setPassword, command.getPassword(), dto.getPassword(), update::setUpdate);
        dto.setCode(command.getCode());
        dto.setName(command.getName());
        dto.setStatus(command.getStatus());
            this.service.update(dto);
        if(update.getUpdate() > 0){
        }
    }
}

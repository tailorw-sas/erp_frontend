package com.kynsoft.report.applications.command.dbconection.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.report.domain.dto.DBConectionDto;
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
        DBConectionDto dto = this.service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setUrl, command.getUrl(), dto.getUrl(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setUsername, command.getUsername(), dto.getUsername(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setPassword, command.getPassword(), dto.getPassword(), update::setUpdate);

        if(update.getUpdate() > 0){
            this.service.update(dto);
        }
    }
}

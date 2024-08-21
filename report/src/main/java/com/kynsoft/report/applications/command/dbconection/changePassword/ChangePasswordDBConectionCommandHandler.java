package com.kynsoft.report.applications.command.dbconection.changePassword;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.rules.dbconection.DBConectionOldPasswordCheckRule;
import com.kynsoft.report.domain.services.IDBConectionService;
import org.springframework.stereotype.Component;

@Component
public class ChangePasswordDBConectionCommandHandler implements ICommandHandler<ChangePasswordDBConectionCommand> {

    private final IDBConectionService service;

    public ChangePasswordDBConectionCommandHandler(IDBConectionService service) {
        this.service = service;
    }

    @Override
    public void handle(ChangePasswordDBConectionCommand command) {
        RulesChecker.checkRule(new DBConectionOldPasswordCheckRule(this.service, command.getOldPassword(), command.getId()));

        DBConectionDto dto = this.service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setPassword, command.getNewPassword(), dto.getPassword(), update::setUpdate);
        if(update.getUpdate() > 0){
            this.service.update(dto);
        }
    }
}

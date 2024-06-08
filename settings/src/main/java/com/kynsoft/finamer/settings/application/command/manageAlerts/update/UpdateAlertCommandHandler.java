package com.kynsoft.finamer.settings.application.command.manageAlerts.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageAlertsDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.manageAlerts.AlertCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IAlertsService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateAlertCommandHandler implements ICommandHandler<UpdateAlertCommand> {
    
    private final IAlertsService service;
    
    public UpdateAlertCommandHandler(final IAlertsService service) {this.service=service;}
    
    @Override
    public void handle(UpdateAlertCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Module ID cannot be null."));
        ManageAlertsDto alertsDTO = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        
        if(UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(alertsDTO::setCode, command.getCode(), alertsDTO.getCode(), update::setUpdate)){
            RulesChecker.checkRule(new AlertCodeMustBeUniqueRule(this.service, command.getCode()));
        }

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(alertsDTO::setDescription, command.getDescription(), alertsDTO.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(alertsDTO::setPopup, command.getPopup(), command.getPopup(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(alertsDTO::setName, command.getName(), alertsDTO.getName(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(alertsDTO::setPopup, command.getPopup(), alertsDTO.getPopup(), update::setUpdate);
        
        this.updateStatus(alertsDTO::setStatus, command.getStatus(), alertsDTO.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(alertsDTO);
        }
            
    }

    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }
}

package com.kynsoft.finamer.settings.application.command.manageAlerts.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageAlertsDto;
import com.kynsoft.finamer.settings.domain.rules.manageAlerts.AlertCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageAlerts.AlertCodeNotNullRule;
import com.kynsoft.finamer.settings.domain.rules.manageAlerts.AlertNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageAlerts.AlertNameNotNullRule;
import com.kynsoft.finamer.settings.domain.services.IAlertsService;
import org.springframework.stereotype.Component;

@Component
public class CreateAlertCommandHandler implements ICommandHandler<CreateAlertCommand> {
    
    private final IAlertsService service;
    public CreateAlertCommandHandler(final IAlertsService service) {this.service = service;}
    
    @Override
    public void handle(CreateAlertCommand command) {
        RulesChecker.checkRule(new AlertCodeNotNullRule(command.getCode()));
        RulesChecker.checkRule(new AlertNameNotNullRule(command.getName()));
        RulesChecker.checkRule(new AlertCodeMustBeUniqueRule(this.service, command.getCode()));
        RulesChecker.checkRule(new AlertNameMustBeUniqueRule(this.service, command.getName()));
        
        service.create(new ManageAlertsDto(command.getId(), command.getCode(), command.getName(), command.getPopup(), command.getStatus(), command.getDescription()));
    }
}

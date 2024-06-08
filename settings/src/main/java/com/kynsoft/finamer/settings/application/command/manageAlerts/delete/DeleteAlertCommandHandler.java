package com.kynsoft.finamer.settings.application.command.manageAlerts.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageAlertsDto;
import com.kynsoft.finamer.settings.domain.services.IAlertsService;
import org.springframework.stereotype.Component;

@Component
public class DeleteAlertCommandHandler implements ICommandHandler<DeleteAlertCommand> {
    
    private final IAlertsService service;
    
    public DeleteAlertCommandHandler(final IAlertsService service) {this.service = service;}
    
    @Override
    public void handle(DeleteAlertCommand command) {
        ManageAlertsDto delete = this.service.findById(command.getId());
        this.service.delete(delete);
    }
}

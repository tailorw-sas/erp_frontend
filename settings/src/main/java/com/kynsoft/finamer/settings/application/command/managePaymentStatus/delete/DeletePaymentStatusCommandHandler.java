package com.kynsoft.finamer.settings.application.command.managePaymentStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerPaymentStatusDto;
import com.kynsoft.finamer.settings.domain.services.IManagerPaymentStatusService;
import org.springframework.stereotype.Component;

@Component
public class DeletePaymentStatusCommandHandler implements ICommandHandler<DeletePaymentStatusCommand> {
    private final IManagerPaymentStatusService service;
    
    public DeletePaymentStatusCommandHandler(final IManagerPaymentStatusService service) {
        this.service = service;
    }
    
    @Override
    public void handle(DeletePaymentStatusCommand command) {
        ManagerPaymentStatusDto dto = service.findById(command.getId());
        
        service.delete(dto);
    }
}

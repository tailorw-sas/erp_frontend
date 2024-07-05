package com.kynsoft.finamer.creditcard.application.command.parameterization.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ParameterizationDto;
import com.kynsoft.finamer.creditcard.domain.services.IParameterizationService;
import org.springframework.stereotype.Component;

@Component
public class DeleteParameterizationCommandHandler implements ICommandHandler<DeleteParameterizationCommand> {

    private final IParameterizationService service;

    public DeleteParameterizationCommandHandler(IParameterizationService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteParameterizationCommand command) {
        ParameterizationDto dto = this.service.findById(command.getId());
        this.service.delete(dto);
    }
}

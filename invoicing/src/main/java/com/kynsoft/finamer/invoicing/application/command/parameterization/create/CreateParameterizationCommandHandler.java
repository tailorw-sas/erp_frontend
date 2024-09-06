package com.kynsoft.finamer.invoicing.application.command.parameterization.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ParameterizationDto;
import com.kynsoft.finamer.invoicing.domain.services.IParameterizationService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CreateParameterizationCommandHandler implements ICommandHandler<CreateParameterizationCommand> {

    private final IParameterizationService service;

    public CreateParameterizationCommandHandler(IParameterizationService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateParameterizationCommand command) {
        ParameterizationDto actual = this.service.findActiveParameterization();

        if(Objects.isNull(actual)){
            this.service.create(new ParameterizationDto(
                    command.getId(), true, command.getSent(),
                    command.getReconciled(), command.getProcessed(),
                    command.getCanceled(), command.getPending()
            ));
        } else {
            this.service.delete(actual);
            this.service.create(new ParameterizationDto(
                    command.getId(), true, command.getSent(),
                    command.getReconciled(), command.getProcessed(),
                    command.getCanceled(), command.getPending()
            ));
        }
    }
}

package com.kynsof.identity.application.command.business.delete;

import com.kynsof.identity.domain.interfaces.service.IBusinessService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class DeleteBusinessCommandHandler implements ICommandHandler<BusinessDeleteCommand> {

    private final IBusinessService service;

    public DeleteBusinessCommandHandler(IBusinessService service) {
        this.service = service;
    }

    @Override
    public void handle(BusinessDeleteCommand command) {

        service.delete(command.getId());
    }

}

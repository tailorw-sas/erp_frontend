package com.kynsof.identity.application.command.customer.deleteAll;

import com.kynsof.identity.domain.interfaces.service.ICustomerService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class DeleteAllCustomerCommandHandler implements ICommandHandler<DeleteAllCustomerCommand> {

    private final ICustomerService serviceImpl;

    public DeleteAllCustomerCommandHandler(ICustomerService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteAllCustomerCommand command) {

        serviceImpl.deleteAll(command.getIds());
    }

}

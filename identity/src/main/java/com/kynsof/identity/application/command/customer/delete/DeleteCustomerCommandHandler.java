package com.kynsof.identity.application.command.customer.delete;

import com.kynsof.identity.domain.dto.CustomerDto;
import com.kynsof.identity.domain.interfaces.service.ICustomerService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class DeleteCustomerCommandHandler implements ICommandHandler<DeleteCustomerCommand> {

    private final ICustomerService service;

    public DeleteCustomerCommandHandler(ICustomerService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteCustomerCommand command) {
        CustomerDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}

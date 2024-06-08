package com.kynsof.identity.application.command.customer.create;

import com.kynsof.identity.domain.dto.CustomerDto;
import com.kynsof.identity.domain.interfaces.service.ICustomerService;
import com.kynsof.identity.domain.rules.customer.CustomerEmailValidateRule;
import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CreateCustomerCommandHandler implements ICommandHandler<CreateCustomerCommand> {

    private final ICustomerService customerService;

    @Autowired
    public CreateCustomerCommandHandler(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void handle(CreateCustomerCommand command) {
        RulesChecker.checkRule(new CustomerEmailValidateRule(command.getEmail()));

        CustomerDto customerDto = new CustomerDto(
                command.getId(),
                command.getFirstName(),
                command.getLastName(),
                command.getEmail(),
                LocalDateTime.now());

        this.customerService.create(customerDto);

    }
}

package com.kynsof.identity.application.command.customer.update;

import com.kynsof.identity.domain.dto.CustomerDto;
import com.kynsof.identity.domain.interfaces.service.ICustomerService;
import com.kynsof.identity.domain.rules.customer.CustomerEmailValidateRule;
import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.UpdateIfNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateCustomerCommandHandler implements ICommandHandler<UpdateCustomerCommand> {

    private final ICustomerService customerService;

    @Autowired
    public UpdateCustomerCommandHandler(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void handle(UpdateCustomerCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "User ID cannot be null."));
        CustomerDto update = this.customerService.findById(command.getId());

        if (UpdateIfNotNull.updateIfStringNotNull(update::setEmail, command.getEmail())) {
            RulesChecker.checkRule(new CustomerEmailValidateRule(command.getEmail()));
        }
        UpdateIfNotNull.updateIfStringNotNull(update::setLastName, command.getLastName());
        UpdateIfNotNull.updateIfStringNotNull(update::setFirstName, command.getFirstName());

        this.customerService.update(update);

    }
}

package com.kynsoft.finamer.settings.application.command.manageCreditCardType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.manageCreditCardType.ManageCreditCardTypeFirstDigitMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageCreditCardTypeService;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageCreditCardTypeCommandHandler implements ICommandHandler<UpdateManageCreditCardTypeCommand> {

    private final IManageCreditCardTypeService service;

    public UpdateManageCreditCardTypeCommandHandler(IManageCreditCardTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageCreditCardTypeCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Credit Card Type ID cannot be null."));

        ManageCreditCardTypeDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate);
        if (UpdateIfNotNull.updateInteger(test::setFirstDigit, command.getFirstDigit(), test.getFirstDigit(), update::setUpdate)) {
            RulesChecker.checkRule(new ManageCreditCardTypeFirstDigitMustBeUniqueRule(this.service, command.getFirstDigit(), command.getId()));
        }

        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
        }

    }

    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

}

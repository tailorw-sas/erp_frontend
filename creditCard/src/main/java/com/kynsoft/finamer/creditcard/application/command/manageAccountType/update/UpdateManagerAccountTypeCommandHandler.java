package com.kynsoft.finamer.creditcard.application.command.manageAccountType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ManageAccountTypeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerAccountTypeDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.services.IManagerAccountTypeService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManagerAccountTypeCommandHandler implements ICommandHandler<UpdateManagerAccountTypeCommand> {

    private final IManagerAccountTypeService service;

    public UpdateManagerAccountTypeCommandHandler(IManagerAccountTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManagerAccountTypeCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Account Type ID cannot be null."));

        ManagerAccountTypeDto accountTypeDto = this.service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        updateFields(accountTypeDto, command, update);

        if (update.getUpdate() > 0) {
            this.service.update(accountTypeDto);
        }
    }

    private void updateFields(ManagerAccountTypeDto accountTypeDto, UpdateManagerAccountTypeCommand command, ConsumerUpdate update) {
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(accountTypeDto::setDescription, command.getDescription(), accountTypeDto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(accountTypeDto::setName, command.getName(), accountTypeDto.getName(), update::setUpdate);
        updateStatus(accountTypeDto::setStatus, command.getStatus(), accountTypeDto.getStatus(), update::setUpdate);
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }
}
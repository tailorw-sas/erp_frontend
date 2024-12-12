package com.kynsoft.finamer.settings.application.command.manageAccountType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ManageAccountTypeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagerAccountTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerAccountTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAccountType.ProducerReplicateAccountTypeService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManagerAccountTypeCommandHandler implements ICommandHandler<UpdateManagerAccountTypeCommand> {

    private final IManagerAccountTypeService service;

    private final ProducerReplicateAccountTypeService producerReplicateAccountTypeService;

    public UpdateManagerAccountTypeCommandHandler(IManagerAccountTypeService service, ProducerReplicateAccountTypeService producerReplicateAccountTypeService) {
        this.service = service;
        this.producerReplicateAccountTypeService = producerReplicateAccountTypeService;
    }

    @Override
    public void handle(UpdateManagerAccountTypeCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Account Type ID cannot be null."));

        ManagerAccountTypeDto accountTypeDto = this.service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        updateFields(accountTypeDto, command, update);

        if (update.getUpdate() > 0) {
            this.service.update(accountTypeDto);
            this.producerReplicateAccountTypeService.replicate(new ManageAccountTypeKafka(
                    accountTypeDto.getId(), accountTypeDto.getCode(), accountTypeDto.getName(),
                    accountTypeDto.getDescription(), accountTypeDto.getStatus().name(), accountTypeDto.isModuleVcc(),
                    accountTypeDto.isModulePayment()
            ));
        }
    }

    private void updateFields(ManagerAccountTypeDto accountTypeDto, UpdateManagerAccountTypeCommand command, ConsumerUpdate update) {
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(accountTypeDto::setDescription, command.getDescription(), accountTypeDto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(accountTypeDto::setName, command.getName(), accountTypeDto.getName(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(accountTypeDto::setModuleVcc, command.isModuleVcc(), accountTypeDto.isModuleVcc(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(accountTypeDto::setModulePayment, command.isModulePayment(), accountTypeDto.isModulePayment(), update::setUpdate);
        updateStatus(accountTypeDto::setStatus, command.getStatus(), accountTypeDto.getStatus(), update::setUpdate);
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }
}
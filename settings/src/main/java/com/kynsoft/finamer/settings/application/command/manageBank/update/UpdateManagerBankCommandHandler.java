package com.kynsoft.finamer.settings.application.command.manageBank.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ManageBankKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerBankService;
import java.util.function.Consumer;

import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageBank.ProducerReplicateBankService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagerBankCommandHandler implements ICommandHandler<UpdateManagerBankCommand> {

    private final IManagerBankService service;

    private final ProducerReplicateBankService producerReplicateBankService;

    public UpdateManagerBankCommandHandler(IManagerBankService service, ProducerReplicateBankService producerReplicateBankService) {
        this.service = service;
        this.producerReplicateBankService = producerReplicateBankService;
    }

    @Override
    public void handle(UpdateManagerBankCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Bank ID cannot be null."));

        ManagerBankDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate);
        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
            this.producerReplicateBankService.replicate(new ManageBankKafka(
                    test.getId(), test.getCode(), test.getName(),
                    test.getDescription(), test.getStatus().name()
            ));
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

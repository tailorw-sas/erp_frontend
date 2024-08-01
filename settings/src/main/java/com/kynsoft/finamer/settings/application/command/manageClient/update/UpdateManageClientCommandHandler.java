package com.kynsoft.finamer.settings.application.command.manageClient.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageClientKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageClientDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerClientService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageClient.ProducerUpdateManageClientService;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageClientCommandHandler implements ICommandHandler<UpdateManageClientCommand> {

    private final IManagerClientService service;

    private final ProducerUpdateManageClientService producerUpdateManageClientService;

    public UpdateManageClientCommandHandler(IManagerClientService service, ProducerUpdateManageClientService producerUpdateManageClientService) {
        this.service = service;
        this.producerUpdateManageClientService = producerUpdateManageClientService;
    }

    @Override
    public void handle(UpdateManageClientCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Client ID cannot be null."));

        ManageClientDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate);
        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(test::setIsNightType, command.getIsNightType(), test.getIsNightType(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
            this.producerUpdateManageClientService.update(new UpdateManageClientKafka(test.getId(), test.getName(), command.getStatus().name(), command.getIsNightType()));
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

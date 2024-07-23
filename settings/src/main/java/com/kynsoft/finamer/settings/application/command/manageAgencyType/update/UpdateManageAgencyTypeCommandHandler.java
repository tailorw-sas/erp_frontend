package com.kynsoft.finamer.settings.application.command.manageAgencyType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageAgencyTypeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAgencyType.ProducerUpdateManageAgencyTypeService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageAgencyTypeCommandHandler implements ICommandHandler<UpdateManageAgencyTypeCommand> {

    private final IManageAgencyTypeService service;
    private final ProducerUpdateManageAgencyTypeService producerUpdateManageAgencyTypeService;

    public UpdateManageAgencyTypeCommandHandler(IManageAgencyTypeService service,
                                                ProducerUpdateManageAgencyTypeService producerUpdateManageAgencyTypeService) {
        this.service = service;
        this.producerUpdateManageAgencyTypeService = producerUpdateManageAgencyTypeService;
    }

    @Override
    public void handle(UpdateManageAgencyTypeCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Agency Type ID cannot be null."));

        ManageAgencyTypeDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        dto.setDescription(command.getDescription());
        this.service.update(dto);

        this.producerUpdateManageAgencyTypeService.update(new UpdateManageAgencyTypeKafka(dto.getId(), dto.getName(), dto.getStatus().name()));
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

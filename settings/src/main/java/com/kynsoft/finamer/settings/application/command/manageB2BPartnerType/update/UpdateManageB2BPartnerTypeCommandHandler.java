package com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageB2BPartnerTypeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageB2BPartnerTypeService;
import java.util.function.Consumer;

import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageB2BPartnerType.ProducerReplicateB2BPartnerTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageB2BPartnerType.ProducerUpdateB2BPartnerTypeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageB2BPartnerTypeCommandHandler implements ICommandHandler<UpdateManageB2BPartnerTypeCommand> {

    private final IManageB2BPartnerTypeService service;
    private final ProducerUpdateB2BPartnerTypeService updateB2BPartnerTypeService;
    public UpdateManageB2BPartnerTypeCommandHandler(IManageB2BPartnerTypeService service,
                                                    ProducerUpdateB2BPartnerTypeService updateB2BPartnerTypeService) {
        this.service = service;

        this.updateB2BPartnerTypeService = updateB2BPartnerTypeService;
    }

    @Override
    public void handle(UpdateManageB2BPartnerTypeCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage B2BPartner Type ID cannot be null."));

        ManageB2BPartnerTypeDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate);
        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
        }

        updateB2BPartnerTypeService.update(UpdateManageB2BPartnerTypeKafka.builder()
                .id(command.getId())
                .status(command.getStatus().name())
                .description(command.getDescription())
                .name(command.getName())
                .build()

        );
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

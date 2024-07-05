package com.kynsoft.finamer.settings.application.command.manageInvoiceType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageInvoiceTypeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceType.ProducerUpdateManageInvoiceTypeService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageInvoiceTypeCommandHandler implements ICommandHandler<UpdateManageInvoiceTypeCommand> {

    private final IManageInvoiceTypeService service;

    private final ProducerUpdateManageInvoiceTypeService producerUpdateManageInvoiceTypeService;

    public UpdateManageInvoiceTypeCommandHandler(IManageInvoiceTypeService service,
                                                 ProducerUpdateManageInvoiceTypeService producerUpdateManageInvoiceTypeService) {
        this.service = service;
        this.producerUpdateManageInvoiceTypeService = producerUpdateManageInvoiceTypeService;
    }

    @Override
    public void handle(UpdateManageInvoiceTypeCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Invoice Type ID cannot be null."));

        ManageInvoiceTypeDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setEnabledToPolicy, command.getEnabledToPolicy(), dto.getEnabledToPolicy(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
            this.producerUpdateManageInvoiceTypeService.update(new UpdateManageInvoiceTypeKafka(dto.getId(), dto.getName()));
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

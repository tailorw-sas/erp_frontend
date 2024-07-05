package com.kynsoft.finamer.settings.application.command.managerTransactionStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageTransactionStatusKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageTransactionStatusService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageTransactionStatus.ProducerUpdateManageTransactionStatusService;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageTransactionStatusCommandHandler implements ICommandHandler<UpdateManageTransactionStatusCommand> {

    private final IManageTransactionStatusService service;
    private final ProducerUpdateManageTransactionStatusService producerUpdateManageTransactionStatusService;

    public UpdateManageTransactionStatusCommandHandler(IManageTransactionStatusService service,
                                                       ProducerUpdateManageTransactionStatusService producerUpdateManageTransactionStatusService) {
        this.service = service;
        this.producerUpdateManageTransactionStatusService = producerUpdateManageTransactionStatusService;
    }

    @Override
    public void handle(UpdateManageTransactionStatusCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Transaction Status ID cannot be null."));

        ManageTransactionStatusDto dto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setEnablePayment, command.getEnablePayment(), dto.getEnablePayment(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setVisible, command.getVisible(), dto.getVisible(), update::setUpdate);
        this.updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        List<ManageTransactionStatusDto> navigate = this.service.findByIds(command.getNavigate().stream().toList());
        dto.setNavigate(navigate);
        this.service.update(dto);
        this.producerUpdateManageTransactionStatusService.update(new UpdateManageTransactionStatusKafka(dto.getId(), dto.getName()));
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }
}

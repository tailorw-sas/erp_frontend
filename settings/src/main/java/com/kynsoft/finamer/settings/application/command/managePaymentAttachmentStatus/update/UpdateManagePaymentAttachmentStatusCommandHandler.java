package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManagePaymentAttachmentStatusKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.settings.domain.dto.ModuleDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus.ManagePaymentAttachmentStatusDefaultMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus.ManagePaymentAttachmentStatusNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageModuleService;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentAttachmentStatus.ProducerUpdateManagePaymentAttachmentStatusService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManagePaymentAttachmentStatusCommandHandler implements ICommandHandler<UpdateManagePaymentAttachmentStatusCommand> {

    private final IManagePaymentAttachmentStatusService service;

    private final IManageModuleService moduleService;

    private final ProducerUpdateManagePaymentAttachmentStatusService producerUpdateManagePaymentAttachmentStatusService;

    public UpdateManagePaymentAttachmentStatusCommandHandler(final IManagePaymentAttachmentStatusService service, IManageModuleService moduleService,
            ProducerUpdateManagePaymentAttachmentStatusService producerUpdateManagePaymentAttachmentStatusService) {
        this.service = service;
        this.moduleService = moduleService;
        this.producerUpdateManagePaymentAttachmentStatusService = producerUpdateManagePaymentAttachmentStatusService;
    }

    @Override
    public void handle(UpdateManagePaymentAttachmentStatusCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Module ID cannot be null."));
        ManagePaymentAttachmentStatusDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        if (UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate)) {
            RulesChecker.checkRule(new ManagePaymentAttachmentStatusNameMustBeUniqueRule(service, command.getName(), command.getId()));
        }

        if (command.getDefaults()) {
            RulesChecker.checkRule(new ManagePaymentAttachmentStatusDefaultMustBeUniqueRule(service, command.getId()));
        }
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setPermissionCode, command.getPermissionCode(), dto.getPermissionCode(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setShow, command.getShow(), dto.getShow(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setDefaults, command.getDefaults(), dto.getDefaults(), update::setUpdate);
        List<ManagePaymentAttachmentStatusDto> managePaymentAttachmentStatusDtoList = service.findByIds(command.getNavigate());
        dto.setRelatedStatuses(managePaymentAttachmentStatusDtoList);
        this.updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        this.updateModule(dto::setModule, command.getModule(), dto.getModule().getId(), update::setUpdate);

        this.service.update(dto);
        this.producerUpdateManagePaymentAttachmentStatusService.update(new UpdateManagePaymentAttachmentStatusKafka(dto.getId(), dto.getName(), command.getStatus().name(), dto.getDefaults()));
//        if (update.getUpdate() > 0) {
//        }
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

    private void updateModule(Consumer<ModuleDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ModuleDto dto = moduleService.findById(newValue);
            setter.accept(dto);
            update.accept(1);

        }
    }
}

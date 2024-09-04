package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentAttachmentStatusKafka;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.settings.domain.dto.ModuleDto;
import com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus.ManagePaymentAttachmentStatusCodeCantBeNullRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus.ManagePaymentAttachmentStatusCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus.ManagePaymentAttachmentStatusCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus.ManagePaymentAttachmentStatusDefaultMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus.ManagePaymentAttachmentStatusNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageModuleService;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentAttachmentStatus.ProducerReplicateManagePaymentAttachmentStatusService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateManagePaymentAttachmentStatusCommandHandler implements ICommandHandler<CreateManagePaymentAttachmentStatusCommand> {

    private final IManagePaymentAttachmentStatusService service;
    private final ProducerReplicateManagePaymentAttachmentStatusService paymentAttachmentStatusService;

    private final IManageModuleService moduleService;

    public CreateManagePaymentAttachmentStatusCommandHandler(final IManagePaymentAttachmentStatusService service,
                                                             ProducerReplicateManagePaymentAttachmentStatusService paymentAttachmentStatusService, IManageModuleService moduleService) {
        this.service = service;
        this.paymentAttachmentStatusService = paymentAttachmentStatusService;
        this.moduleService = moduleService;
    }

    @Override
    public void handle(CreateManagePaymentAttachmentStatusCommand command) {
        RulesChecker.checkRule(new ManagePaymentAttachmentStatusCodeCantBeNullRule(command.getCode()));
        RulesChecker.checkRule(new ManagePaymentAttachmentStatusCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagePaymentAttachmentStatusCodeMustBeUniqueRule(service, command.getCode(), command.getId()));
        RulesChecker.checkRule(new ManagePaymentAttachmentStatusNameMustBeUniqueRule(service, command.getName(), command.getId()));
        if (command.getDefaults()) {
            RulesChecker.checkRule(new ManagePaymentAttachmentStatusDefaultMustBeUniqueRule(service, command.getId()));
        }

        List<ManagePaymentAttachmentStatusDto> managePaymentAttachmentStatusDtoList = service.findByIds(command.getNavigate());

        ModuleDto moduleDto = moduleService.findById(command.getModule());

        service.create(
                new ManagePaymentAttachmentStatusDto(command.getId(), command.getCode(), command.getName(),
                        command.getStatus(),  moduleDto, command.getShow(), command.getDefaults(), command.getPermissionCode(),
                        command.getDescription(), managePaymentAttachmentStatusDtoList));

        this.paymentAttachmentStatusService.create(new ReplicateManagePaymentAttachmentStatusKafka(command.getId(), command.getCode(), command.getName(), command.getStatus().name(), command.getDefaults()));
    }
}

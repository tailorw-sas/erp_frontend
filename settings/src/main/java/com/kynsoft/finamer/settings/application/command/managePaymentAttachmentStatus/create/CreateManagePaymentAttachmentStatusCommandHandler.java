package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus.ManagePaymentAttachmentStatusCodeCantBeNullRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus.ManagePaymentAttachmentStatusCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus.ManagePaymentAttachmentStatusCodeSizeRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagePaymentAttachmentStatusCommandHandler implements ICommandHandler<CreateManagePaymentAttachmentStatusCommand> {

    private final IManagePaymentAttachmentStatusService service;

    public CreateManagePaymentAttachmentStatusCommandHandler(final IManagePaymentAttachmentStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagePaymentAttachmentStatusCommand command) {
        RulesChecker.checkRule(new ManagePaymentAttachmentStatusCodeCantBeNullRule(command.getCode()));
        RulesChecker.checkRule(new ManagePaymentAttachmentStatusCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagePaymentAttachmentStatusCodeMustBeUniqueRule(service, command.getCode(), command.getId()));

        service.create(new ManagePaymentAttachmentStatusDto(command.getId(), command.getCode(), command.getName(), command.getStatus(), command.getNavigate(), command.getModule(), command.getShow(), command.getPermissionCode(), command.getDescription()));
    }
}

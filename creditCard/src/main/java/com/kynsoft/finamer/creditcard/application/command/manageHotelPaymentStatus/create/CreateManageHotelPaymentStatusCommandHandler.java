package com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelPaymentStatusDto;
import com.kynsoft.finamer.creditcard.domain.rules.manageHotelPaymentStatus.*;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelPaymentStatusService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageHotelPaymentStatusCommandHandler implements ICommandHandler<CreateManageHotelPaymentStatusCommand> {

    private final IManageHotelPaymentStatusService service;

    public CreateManageHotelPaymentStatusCommandHandler(IManageHotelPaymentStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageHotelPaymentStatusCommand command) {
        RulesChecker.checkRule(new ManageHotelPaymentStatusNameMustNotBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageHotelPaymentStatusCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageHotelPaymentStatusCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        if (command.isInProgress()){
            RulesChecker.checkRule(new ManageHotelPaymentStatusInProgressMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCompleted()){
            RulesChecker.checkRule(new ManageHotelPaymentStatusCompletedMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCancelled()){
            RulesChecker.checkRule(new ManageHotelPaymentStatusCancelledMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isApplied()){
            RulesChecker.checkRule(new ManageHotelPaymentStatusAppliedMustBeUniqueRule(this.service, command.getId()));
        }

        ManageHotelPaymentStatusDto dto = new ManageHotelPaymentStatusDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus(),
                command.getDescription(),
                command.isInProgress(),
                command.isCompleted(),
                command.isCancelled(),
                command.isApplied()
        );

        this.service.create(dto);
    }
}

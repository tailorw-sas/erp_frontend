package com.kynsoft.finamer.creditcard.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateManageAgencyCommandHandler implements ICommandHandler<CreateManageAgencyCommand> {

    private final IManageAgencyService service;
    @Autowired
    public CreateManageAgencyCommandHandler(IManageAgencyService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageAgencyCommand command) {
        service.create(new ManageAgencyDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getBookingCouponFormat()
        ));
    }
}

package com.kynsoft.finamer.insis.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.insis.domain.services.IBookingService;
import com.kynsoft.finamer.insis.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateManageAgencyCommandHandler implements ICommandHandler<CreateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IBookingService bookingService;

    public CreateManageAgencyCommandHandler(IManageAgencyService service, IBookingService bookingService){
        this.service = service;
        this.bookingService = bookingService;
    }

    @Override
    public void handle(CreateManageAgencyCommand command) {
        ManageAgencyDto dto = new ManageAgencyDto(command.getId(), command.getCode(), command.getName(), command.getAgencyAlias(), command.getStatus(), command.isDeleted(), null);

        UUID agencyId = service.create(dto);

        dto.setId(agencyId);

        updateAgencyInBooking(dto);
    }

    private void updateAgencyInBooking(ManageAgencyDto agencyDto){
        bookingService.updateAgencyByAgencyCode(agencyDto);
    }
}

package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import org.springframework.stereotype.Component;

@Component
public class CreateRoomRateCommandHandler implements ICommandHandler<CreateRoomRateCommand> {

    private final IManageRoomRateService roomRateService;
    private final IManageBookingService bookingService;

    public CreateRoomRateCommandHandler(IManageRoomRateService roomRateService, IManageBookingService bookingService) {
        this.roomRateService = roomRateService;
        this.bookingService = bookingService;
    }

    @Override
    public void handle(CreateRoomRateCommand command) {
        ManageBookingDto bookingDto = bookingService.findById(command.getBooking());

        roomRateService.create(new ManageRoomRateDto(
                command.getId(),
                null,
                command.getCheckIn(),
                command.getCheckOut(),
                command.getInvoiceAmount(),
                command.getRoomNumber(),
                command.getAdults(),
                command.getChildren(),
                command.getRateAdult(),
                command.getRateChild(),
                command.getHotelAmount(),
                command.getRemark(),
                bookingDto
        ));
    }
}

package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import org.springframework.stereotype.Component;

@Component
public class UpdateRoomRateCommandHandler implements ICommandHandler<UpdateRoomRateCommand> {

    private final IManageRoomRateService roomRateService;
    private final IManageBookingService bookingService;
    private final IManageInvoiceService invoiceService;

    public UpdateRoomRateCommandHandler(IManageRoomRateService roomRateService, IManageBookingService bookingService, IManageInvoiceService invoiceService) {
        this.roomRateService = roomRateService;
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
    }

    @Override
    public void handle(UpdateRoomRateCommand command) {
        ManageRoomRateDto dto = this.roomRateService.findById(command.getId());


        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateLocalDateTime(dto::setCheckIn, command.getCheckIn(), dto.getCheckIn(), update::setUpdate);
        UpdateIfNotNull.updateLocalDateTime(dto::setCheckOut, command.getCheckOut(), dto.getCheckOut(), update::setUpdate);
        UpdateIfNotNull.updateDouble(dto::setInvoiceAmount, command.getInvoiceAmount(), dto.getInvoiceAmount(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setRoomNumber, command.getRoomNumber(), dto.getRoomNumber(), update::setUpdate);
        UpdateIfNotNull.updateInteger(dto::setAdults, command.getAdults(), dto.getAdults(), update::setUpdate);
        UpdateIfNotNull.updateInteger(dto::setChildren, command.getChildren(), dto.getChildren(), update::setUpdate);
        UpdateIfNotNull.updateDouble(dto::setRateAdult, command.getRateAdult(), dto.getRateAdult(), update::setUpdate);
        UpdateIfNotNull.updateDouble(dto::setRateChild, command.getRateChild(), dto.getRateChild(), update::setUpdate);
        UpdateIfNotNull.updateDouble(dto::setHotelAmount, command.getHotelAmount(), dto.getHotelAmount(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setRemark, command.getRemark(), dto.getRemark(), update::setUpdate);



        UpdateIfNotNull.updateEntity(dto::setBooking, command.getBooking(), dto.getBooking().getId(), update::setUpdate, this.bookingService::findById);

        if (!command.getInvoiceAmount().equals(dto.getInvoiceAmount())) {


            bookingService.calculateInvoiceAmount(this.bookingService.findById(dto.getBooking().getId()));
            bookingService.calculateHotelAmount(this.bookingService.findById(dto.getBooking().getId()));
            invoiceService.calculateInvoiceAmount(this.invoiceService.findById(dto.getBooking().getInvoice().getId()));
        }


        if (update.getUpdate() > 0) {
            this.roomRateService.update(dto);
        }
    }
}

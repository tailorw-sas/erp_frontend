package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateChickInAndCheckOut.UpdateBookingCalculateCheckIntAndCheckOutCommand;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.rules.manageRoomRate.ManageRoomRateCheckAdultsAndChildrenRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageRoomRate.ManageRoomRateCheckInCheckOutRule;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        RulesChecker.checkRule(new ManageRoomRateCheckInCheckOutRule(command.getCheckIn(), command.getCheckOut()));
        RulesChecker.checkRule(new ManageRoomRateCheckAdultsAndChildrenRule(command.getAdults(), command.getChildren()));

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateLocalDateTime(dto::setCheckIn, command.getCheckIn(), dto.getCheckIn(), update::setUpdate);
        UpdateIfNotNull.updateLocalDateTime(dto::setCheckOut, command.getCheckOut(), dto.getCheckOut(), update::setUpdate);
        UpdateIfNotNull.updateDouble(dto::setInvoiceAmount, command.getInvoiceAmount(), dto.getInvoiceAmount(), update::setUpdate);
        UpdateIfNotNull.updateInteger(dto::setAdults, command.getAdults(), dto.getAdults(), update::setUpdate);
        UpdateIfNotNull.updateInteger(dto::setChildren, command.getChildren(), dto.getChildren(), update::setUpdate);

        UpdateIfNotNull.updateLong(dto::setNights, this.calculateNights(dto.getCheckIn(), dto.getCheckOut()), dto.getNights(), update::setUpdate);

        UpdateIfNotNull.updateDouble(dto::setRateAdult, this.calculateRateAdult(dto.getInvoiceAmount(), dto.getNights(), dto.getAdults()), dto.getRateAdult(), update::setUpdate);
        UpdateIfNotNull.updateDouble(dto::setRateChild, this.calculateRateChild(dto.getInvoiceAmount(), dto.getNights(), dto.getChildren()), dto.getRateChild(), update::setUpdate);

        UpdateIfNotNull.updateDouble(dto::setHotelAmount, command.getHotelAmount(), dto.getHotelAmount(), update::setUpdate);

        if (!command.getInvoiceAmount().equals(dto.getInvoiceAmount())) {

            bookingService.calculateInvoiceAmount(this.bookingService.findById(dto.getBooking().getId()));
            bookingService.calculateHotelAmount(this.bookingService.findById(dto.getBooking().getId()));
            invoiceService.calculateInvoiceAmount(this.invoiceService.findById(dto.getBooking().getInvoice().getId()));
        }

        if (update.getUpdate() > 0) {
            this.roomRateService.update(dto);
            command.getMediator().send(new UpdateBookingCalculateCheckIntAndCheckOutCommand(dto.getBooking().getId()));
        }
    }

    private Double calculateRateAdult(Double rateAmount, Long nights, Integer adults) {
        return adults == 0 ? 0.0 : rateAmount/(nights*adults);
    }

    private Double calculateRateChild(Double rateAmount, Long nights, Integer children) {
        return children == 0 ? 0.0 : rateAmount/(nights*children);
    }

    private Long calculateNights(LocalDateTime checkIn, LocalDateTime checkOut) {
        return ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());
    }
}

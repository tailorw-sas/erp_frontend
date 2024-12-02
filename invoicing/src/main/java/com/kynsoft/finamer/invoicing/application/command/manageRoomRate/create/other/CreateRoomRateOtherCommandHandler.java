package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.other;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingAdults.UpdateBookingCalculateBookingAdultsCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingAmount.UpdateBookingCalculateBookingAmountCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingChildren.UpdateBookingCalculateBookingChildrenCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateChickInAndCheckOut.UpdateBookingCalculateCheckIntAndCheckOutCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateDueAmount.UpdateBookingCalculateDueAmountCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateHotelAmount.UpdateBookingCalculateHotelAmountCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateRateAdult.UpdateBookingCalculateRateAdultCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateRateChild.UpdateBookingCalculateRateChildCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.calculateDueAmount.UpdateInvoiceCalculateDueAmountCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.calculateInvoiceAmount.UpdateInvoiceCalculateInvoiceAmountCommand;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageBooking.ProducerReplicateManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerUpdateManageInvoiceService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class CreateRoomRateOtherCommandHandler implements ICommandHandler<CreateRoomRateOtherCommand> {

    private final IManageRoomRateService roomRateService;
    private final IManageBookingService bookingService;
    private final IManageInvoiceService invoiceService;

    private final ProducerUpdateManageInvoiceService producerUpdateManageInvoiceService;
    private final ProducerReplicateManageBookingService producerReplicateManageBookingService;

    public CreateRoomRateOtherCommandHandler(IManageRoomRateService roomRateService,
            IManageBookingService bookingService,
            IManageInvoiceService invoiceService,
            ProducerUpdateManageInvoiceService producerUpdateManageInvoiceService,
            ProducerReplicateManageBookingService producerReplicateManageBookingService) {
        this.roomRateService = roomRateService;
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
        this.producerUpdateManageInvoiceService = producerUpdateManageInvoiceService;
        this.producerReplicateManageBookingService = producerReplicateManageBookingService;
    }

    @Override
    public void handle(CreateRoomRateOtherCommand command) {
        ManageBookingDto bookingDto = bookingService.findById(command.getBooking());

        long nights = this.calculateNights(command.getCheckIn(), command.getCheckOut());
        double rateAdult = this.calculateRateAdult(command.getInvoiceAmount(), nights, command.getAdults());
        double rateChild = this.calculateRateChild(command.getInvoiceAmount(), nights, command.getChildren());

        roomRateService.create(new ManageRoomRateDto(
                command.getId(),
                null,
                command.getCheckIn(),
                command.getCheckOut(),
                command.getInvoiceAmount(),
                command.getRoomNumber(),
                command.getAdults(),
                command.getChildren(),
                rateAdult,
                rateChild,
                command.getHotelAmount(),
                command.getRemark(),
                bookingDto,
                null,
                nights,
                false
        ));

        ManageBookingDto updateBookingDto = this.bookingService.findById(bookingDto.getId());

        command.getMediator().send(new UpdateBookingCalculateCheckIntAndCheckOutCommand(updateBookingDto));

        command.getMediator().send(new UpdateBookingCalculateDueAmountCommand(updateBookingDto, command.getInvoiceAmount()));

        command.getMediator().send(new UpdateBookingCalculateBookingAmountCommand(updateBookingDto));
        command.getMediator().send(new UpdateBookingCalculateHotelAmountCommand(updateBookingDto));

        command.getMediator().send(new UpdateBookingCalculateBookingAdultsCommand(updateBookingDto));
        command.getMediator().send(new UpdateBookingCalculateBookingChildrenCommand(updateBookingDto));

        command.getMediator().send(new UpdateBookingCalculateRateChildCommand(updateBookingDto));
        command.getMediator().send(new UpdateBookingCalculateRateAdultCommand(updateBookingDto));

        this.bookingService.update(updateBookingDto);

        //Actualizando la invoice
        ManageInvoiceDto invoiceDto = this.invoiceService.findById(updateBookingDto.getInvoice().getId());
        command.getMediator().send(new UpdateInvoiceCalculateInvoiceAmountCommand(invoiceDto));
        //command.getMediator().send(new UpdateInvoiceCalculateDueAmountCommand(invoiceDto, command.getInvoiceAmount()));
        this.invoiceService.update(invoiceDto);

        try {
            //TODO: aqui se envia para actualizar booking y invoice para payment
            this.producerReplicateManageBookingService.create(updateBookingDto);
            this.producerUpdateManageInvoiceService.update(invoiceDto);
        } catch (Exception e) {
        }
    }

    private Double calculateRateAdult(Double rateAmount, Long nights, Integer adults) {
        return adults == 0 ? 0.0 : rateAmount / (nights * adults);
    }

    private Double calculateRateChild(Double rateAmount, Long nights, Integer children) {
        return children == 0 ? 0.0 : rateAmount / (nights * children);
    }

    private Long calculateNights(LocalDateTime checkIn, LocalDateTime checkOut) {
        return ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());
    }

}

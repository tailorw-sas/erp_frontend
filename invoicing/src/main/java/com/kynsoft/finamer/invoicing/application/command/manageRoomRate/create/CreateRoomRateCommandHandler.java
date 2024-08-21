package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageBooking.ProducerReplicateManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerUpdateManageInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class CreateRoomRateCommandHandler implements ICommandHandler<CreateRoomRateCommand> {

    private final IManageRoomRateService roomRateService;
    private final IManageBookingService bookingService;
    private final IManageInvoiceService invoiceService;

    private final ProducerUpdateManageInvoiceService producerUpdateManageInvoiceService;
    private final ProducerReplicateManageBookingService producerReplicateManageBookingService;

    public CreateRoomRateCommandHandler(IManageRoomRateService roomRateService,
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
                bookingDto,
                null, null));

        ManageBookingDto updateBookingDto = this.bookingService.findById(bookingDto.getId());
        bookingService.calculateInvoiceAmount(updateBookingDto);
        bookingService.calculateHotelAmount(this.bookingService.findById(bookingDto.getId()));
        ManageInvoiceDto updInvoiceDto = this.invoiceService.findById(bookingDto.getInvoice().getId());
        invoiceService.calculateInvoiceAmount(updInvoiceDto);

        try {
            //TODO: aqui se envia para actualizar booking y invoice para payment
            this.producerReplicateManageBookingService.create(updateBookingDto);
            this.producerUpdateManageInvoiceService.update(updInvoiceDto);
        } catch (Exception e) {
        }
    }
}

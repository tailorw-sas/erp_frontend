package com.kynsoft.finamer.invoicing.application.command.manageBooking.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;

import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingCommand;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageBooking.ProducerReplicateManageBookingService;
import org.springframework.stereotype.Component;

@Component
public class CreateManyBookingCommandHandler implements ICommandHandler<CreateManyBookingsCommand> {

    private final IManageBookingService bookingService;
    private final IManageInvoiceService invoiceService;
    private final IManageRatePlanService ratePlanService;
    private final IManageNightTypeService nightTypeService;
    private final IManageRoomTypeService roomTypeService;
    private final IManageRoomCategoryService roomCategoryService;

    private final ProducerReplicateManageBookingService producerReplicateManageBookingService;

    public CreateManyBookingCommandHandler(IManageBookingService bookingService,
            IManageInvoiceService invoiceService,
            IManageRatePlanService ratePlanService, IManageNightTypeService nightTypeService,
            IManageRoomTypeService roomTypeService, IManageRoomCategoryService roomCategoryService,
            ProducerReplicateManageBookingService producerReplicateManageBookingService) {
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
        this.ratePlanService = ratePlanService;
        this.nightTypeService = nightTypeService;
        this.roomTypeService = roomTypeService;
        this.roomCategoryService = roomCategoryService;
        this.producerReplicateManageBookingService = producerReplicateManageBookingService;
    }

    @Override
    public void handle(CreateManyBookingsCommand command) {

        for (int i = 0; i < command.getBookings().size(); i++) {
            CreateBookingCommand element = command.getBookings().get(i);

            ManageInvoiceDto invoiceDto = element.getInvoice() != null
                    ? this.invoiceService.findById(element.getInvoice())
                    : null;
            ManageRatePlanDto ratePlanDto = element.getRatePlan() != null
                    ? this.ratePlanService.findById(element.getRatePlan())
                    : null;
            ManageNightTypeDto nightTypeDto = element.getNightType() != null
                    ? this.nightTypeService.findById(element.getNightType())
                    : null;
            ManageRoomTypeDto roomTypeDto = element.getRoomType() != null
                    ? this.roomTypeService.findById(element.getRoomType())
                    : null;
            ManageRoomCategoryDto roomCategoryDto = element.getRoomCategory() != null
                    ? this.roomCategoryService.findById(element.getRoomCategory())
                    : null;

            ManageBookingDto newBookingDto = new ManageBookingDto(
                    element.getId(),
                    null,
                    null,
                    element.getHotelCreationDate(),
                    element.getBookingDate(),
                    element.getCheckIn(),
                    element.getCheckOut(),
                    element.getHotelBookingNumber(),
                    element.getFullName(),
                    element.getFirstName(), element.getLastName(),
                    element.getInvoiceAmount(),
                    element.getInvoiceAmount(),
                    element.getRoomNumber(),
                    element.getCouponNumber(),
                    element.getAdults(),
                    element.getChildren(),
                    element.getRateAdult(),
                    element.getRateChild(),
                    element.getHotelInvoiceNumber(),
                    element.getFolioNumber(),
                    element.getHotelAmount(),
                    element.getDescription(),
                    invoiceDto,
                    ratePlanDto,
                    nightTypeDto,
                    roomTypeDto,
                    roomCategoryDto,
                    null, null, null);
            bookingService.create(newBookingDto);

            //TODO: aqui se envia el booking para payment
            try {
                this.producerReplicateManageBookingService.create(newBookingDto);
            } catch (Exception e) {
            }

        }

    }
}

package com.kynsoft.finamer.invoicing.application.command.manageBooking.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageNightTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.manageBooking.ManageBookingHotelBookingNumberValidationRule;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckInvoiceTypeIncomeNotMoreBookingRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageBooking.ProducerReplicateManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerUpdateManageInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class CreateBookingCommandHandler implements ICommandHandler<CreateBookingCommand> {

        private final IManageBookingService bookingService;
        private final IManageInvoiceService invoiceService;
        private final IManageRatePlanService ratePlanService;
        private final IManageNightTypeService nightTypeService;
        private final IManageRoomTypeService roomTypeService;
        private final IManageRoomCategoryService roomCategoryService;

        private final IInvoiceCloseOperationService closeOperationService;
        private final ProducerUpdateManageInvoiceService producerUpdateManageInvoiceService;
        private final ProducerReplicateManageBookingService producerReplicateManageBookingService;

        public CreateBookingCommandHandler(IManageBookingService bookingService, IManageInvoiceService invoiceService,
                        IManageRatePlanService ratePlanService, IManageNightTypeService nightTypeService,
                        IManageRoomTypeService roomTypeService, IManageRoomCategoryService roomCategoryService,
                        IInvoiceCloseOperationService closeOperationService,
                        ProducerUpdateManageInvoiceService producerUpdateManageInvoiceService,
                        ProducerReplicateManageBookingService producerReplicateManageBookingService) {
                this.bookingService = bookingService;
                this.invoiceService = invoiceService;
                this.ratePlanService = ratePlanService;
                this.nightTypeService = nightTypeService;
                this.roomTypeService = roomTypeService;
                this.roomCategoryService = roomCategoryService;
                this.closeOperationService = closeOperationService;
                this.producerUpdateManageInvoiceService = producerUpdateManageInvoiceService;
                this.producerReplicateManageBookingService = producerReplicateManageBookingService;
        }

        @Override
        public void handle(CreateBookingCommand command) {

                ManageInvoiceDto invoiceDto = command.getInvoice() != null
                                ? this.invoiceService.findById(command.getInvoice())
                                : null;

                // Agregue esta regla para validar que un invoice de type INCOME no puede tener
                // mas de un Booking.
                if (invoiceDto != null && invoiceDto.getInvoiceType() != null) {
                        RulesChecker.checkRule(
                                        new CheckInvoiceTypeIncomeNotMoreBookingRule(invoiceDto.getInvoiceType()));
                }

                if (command.getHotelBookingNumber().length() > 2) {

                        if (invoiceDto != null && invoiceDto.getHotel().getId() != null
                                        && invoiceDto.getInvoiceType() != null
                                        && !invoiceDto.getInvoiceType().equals(EInvoiceType.CREDIT)) {
                                RulesChecker.checkRule(new ManageBookingHotelBookingNumberValidationRule(bookingService,
                                                command.getHotelBookingNumber()
                                                                .split("\\s+")[command.getHotelBookingNumber()
                                                                                .split("\\s+").length - 1],
                                                invoiceDto.getHotel().getId()));
                        }
                }

                ManageNightTypeDto nightTypeDto = command.getNightType() != null
                                ? this.nightTypeService.findById(command.getNightType())
                                : null;
                ManageRoomTypeDto roomTypeDto = command.getRoomType() != null
                                ? this.roomTypeService.findById(command.getRoomType())
                                : null;
                ManageRoomCategoryDto roomCategoryDto = command.getRoomCategory() != null
                                ? this.roomCategoryService.findById(command.getRoomCategory())
                                : null;
                ManageRatePlanDto ratePlanDto = command.getRatePlan() != null
                                ? this.ratePlanService.findById(command.getRatePlan())
                                : null;

                ManageBookingDto newBooking = new ManageBookingDto(
                                command.getId(),
                                null,
                                null,
                                command.getHotelCreationDate(),
                                command.getBookingDate(),
                                command.getCheckIn(),
                                command.getCheckOut(),
                                command.getHotelBookingNumber(),
                                command.getFullName(),
                                command.getFirstName(),
                                command.getLastName(),
                                command.getInvoiceAmount(),
                                command.getInvoiceAmount(),
                                command.getRoomNumber(),
                                command.getCouponNumber(),
                                command.getAdults(),
                                command.getChildren(),
                                command.getRateAdult(),
                                command.getRateChild(),
                                command.getHotelInvoiceNumber(),
                                command.getFolioNumber(),
                                command.getHotelAmount(),
                                command.getDescription(),
                                invoiceDto,
                                ratePlanDto,
                                nightTypeDto,
                                roomTypeDto,
                                roomCategoryDto,
                                null, null, null);
                bookingService.create(newBooking);

                try {
                        // TODO: aqui se envia el booking para payment
                        this.producerReplicateManageBookingService.create(newBooking);
                } catch (Exception e) {
                }

                ManageInvoiceDto invoiceUpdate = this.invoiceService.findById(invoiceDto.getId());
                invoiceService.calculateInvoiceAmount(invoiceUpdate);
                try {
                        // TODO: aqui se envia para actualizar el invoice para payment
                        this.producerUpdateManageInvoiceService.update(invoiceUpdate);
                } catch (Exception e) {
                }
        }
}

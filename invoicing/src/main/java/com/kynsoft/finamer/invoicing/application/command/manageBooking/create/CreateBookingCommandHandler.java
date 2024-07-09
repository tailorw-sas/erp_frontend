package com.kynsoft.finamer.invoicing.application.command.manageBooking.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageNightTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.invoicing.domain.rules.manageBooking.ManageBookingHotelBookingNumberValidationRule;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.invoicing.domain.services.IManageNightTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomCategoryService;
import org.springframework.stereotype.Component;

@Component
public class CreateBookingCommandHandler implements ICommandHandler<CreateBookingCommand> {

        private final IManageBookingService bookingService;
        private final IManageInvoiceService invoiceService;
        private final IManageRatePlanService ratePlanService;
        private final IManageNightTypeService nightTypeService;
        private final IManageRoomTypeService roomTypeService;
        private final IManageRoomCategoryService roomCategoryService;

        public CreateBookingCommandHandler(IManageBookingService bookingService, IManageInvoiceService invoiceService,
                        IManageRatePlanService ratePlanService, IManageNightTypeService nightTypeService,
                        IManageRoomTypeService roomTypeService, IManageRoomCategoryService roomCategoryService) {
                this.bookingService = bookingService;
                this.invoiceService = invoiceService;
                this.ratePlanService = ratePlanService;
                this.nightTypeService = nightTypeService;
                this.roomTypeService = roomTypeService;
                this.roomCategoryService = roomCategoryService;
        }

        @Override
        public void handle(CreateBookingCommand command) {

                ManageInvoiceDto invoiceDto = command.getInvoice() != null
                                ? this.invoiceService.findById(command.getInvoice())
                                : null;

                if (command.getHotelBookingNumber().length() > 2) {
                        int endIndex = command.getHotelBookingNumber().length() - 2;

                        if (invoiceDto != null && invoiceDto.getHotel().getId() != null) {
                                RulesChecker.checkRule(new ManageBookingHotelBookingNumberValidationRule(bookingService,
                                                command.getHotelBookingNumber().substring(endIndex,
                                                                command.getHotelBookingNumber().length()),
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

                bookingService.create(new ManageBookingDto(
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
                                null));
        }
}

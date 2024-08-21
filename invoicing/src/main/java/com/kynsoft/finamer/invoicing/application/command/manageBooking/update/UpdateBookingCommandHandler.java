package com.kynsoft.finamer.invoicing.application.command.manageBooking.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class UpdateBookingCommandHandler implements ICommandHandler<UpdateBookingCommand> {

        private final IManageBookingService bookingService;
        private final IManageInvoiceService invoiceService;
        private final IManageRatePlanService ratePlanService;
        private final IManageNightTypeService nightTypeService;
        private final IManageRoomTypeService roomTypeService;
        private final IManageRoomCategoryService roomCategoryService;
        private final IInvoiceCloseOperationService closeOperationService;

        public UpdateBookingCommandHandler(IManageBookingService bookingService, IManageInvoiceService invoiceService,
                        IManageRatePlanService ratePlanService, IManageNightTypeService nightTypeService,
                        IManageRoomTypeService roomTypeService, IManageRoomCategoryService roomCategoryService,  IInvoiceCloseOperationService closeOperationService) {
                this.bookingService = bookingService;
                this.invoiceService = invoiceService;
                this.ratePlanService = ratePlanService;
                this.nightTypeService = nightTypeService;
                this.roomTypeService = roomTypeService;
                this.roomCategoryService = roomCategoryService;
                this.closeOperationService = closeOperationService;

        }

        @Override
        public void handle(UpdateBookingCommand command) {
                ManageBookingDto dto = this.bookingService.findById(command.getId());

                command.setInvoice(dto.getInvoice().getId());
                ConsumerUpdate update = new ConsumerUpdate();

                UpdateIfNotNull.updateLocalDateTime(dto::setHotelCreationDate, command.getHotelCreationDate(),
                                dto.getHotelCreationDate(), update::setUpdate);

             
        

        UpdateIfNotNull.updateLocalDateTime(dto::setBookingDate,command.getBookingDate(),dto.getBookingDate(),update::setUpdate);UpdateIfNotNull.updateLocalDateTime(dto::setCheckIn,command.getCheckIn(),dto.getCheckIn(),update::setUpdate);UpdateIfNotNull.updateLocalDateTime(dto::setCheckOut,command.getCheckOut(),dto.getCheckOut(),update::setUpdate);UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setHotelBookingNumber,command.getHotelBookingNumber(),dto.getHotelBookingNumber(),update::setUpdate);UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setFullName,command.getFullName(),dto.getFullName(),update::setUpdate);UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setFirstName,command.getFirstName(),dto.getFirstName(),update::setUpdate);UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setLastName,command.getLastName(),dto.getLastName(),update::setUpdate);

        UpdateIfNotNull.updateDouble(dto::setInvoiceAmount,command.getInvoiceAmount(),dto.getInvoiceAmount(),update::setUpdate);UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setRoomNumber,command.getRoomNumber(),dto.getRoomNumber(),update::setUpdate);UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCouponNumber,command.getCouponNumber(),dto.getCouponNumber(),update::setUpdate);UpdateIfNotNull.updateInteger(dto::setAdults,command.getAdults(),dto.getAdults(),update::setUpdate);UpdateIfNotNull.updateInteger(dto::setChildren,command.getChildren(),dto.getChildren(),update::setUpdate);UpdateIfNotNull.updateDouble(dto::setRateAdult,command.getRateAdult(),dto.getRateAdult(),update::setUpdate);UpdateIfNotNull.updateDouble(dto::setRateChild,command.getRateChild(),dto.getRateChild(),update::setUpdate);UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setHotelInvoiceNumber,command.getHotelInvoiceNumber(),dto.getHotelInvoiceNumber(),update::setUpdate);UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setFolioNumber,command.getFolioNumber(),dto.getFolioNumber(),update::setUpdate);UpdateIfNotNull.updateDouble(dto::setHotelAmount,command.getHotelAmount(),dto.getHotelAmount(),update::setUpdate);UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription,command.getDescription(),dto.getDescription(),update::setUpdate);

        this.updateEntity(dto::setInvoice,command.getInvoice(),dto.getInvoice()!=null?dto.getInvoice().getId():null,update::setUpdate,this.invoiceService::findById);this.updateEntity(dto::setRatePlan,command.getRatePlan(),dto.getRatePlan()!=null?dto.getRatePlan().getId():null,update::setUpdate,this.ratePlanService::findById);this.updateEntity(dto::setNightType,command.getNightType(),dto.getNightType()!=null?dto.getNightType().getId():null,update::setUpdate,this.nightTypeService::findById);this.updateEntity(dto::setRoomType,command.getRoomType(),dto.getRoomType()!=null?dto.getRoomType().getId():null,update::setUpdate,this.roomTypeService::findById);this.updateEntity(dto::setRoomCategory,command.getRoomCategory(),dto.getRoomCategory()!=null?dto.getRoomCategory().getId():null,update::setUpdate,this.roomCategoryService::findById);

        if(update.getUpdate()>0)

        {
                        this.bookingService.update(dto);
                }
        }

        private <T> void updateEntity(Consumer<T> setter, UUID newValue, UUID oldValue, Consumer<Integer> update,
                        Function<UUID, T> findByIdFunction) {
                if (newValue != null && !newValue.equals(oldValue)) {
                        T entity = findByIdFunction.apply(newValue);
                        setter.accept(entity);
                        update.accept(1);
                }
        }
}

package com.kynsoft.finamer.invoicing.application.command.manageBooking.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerUpdateManageInvoiceService;
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
    private final ProducerReplicateManageInvoiceService producerUpdateManageInvoiceService;

    public UpdateBookingCommandHandler(IManageBookingService bookingService,
                                       IManageRatePlanService ratePlanService, IManageNightTypeService nightTypeService,
                                       IManageRoomTypeService roomTypeService, IManageRoomCategoryService roomCategoryService,
                                       IInvoiceCloseOperationService closeOperationService,
                                       ProducerReplicateManageInvoiceService producerUpdateManageInvoiceService,
                                       IManageInvoiceService invoiceService) {
        this.bookingService = bookingService;
        this.ratePlanService = ratePlanService;
        this.nightTypeService = nightTypeService;
        this.roomTypeService = roomTypeService;
        this.roomCategoryService = roomCategoryService;
        this.closeOperationService = closeOperationService;
        this.producerUpdateManageInvoiceService = producerUpdateManageInvoiceService;
        this.invoiceService = invoiceService;
    }

    @Override
    public void handle(UpdateBookingCommand command) {
        ManageBookingDto dto = this.bookingService.findById(command.getId());
        //RulesChecker.checkRule(new ManageBookingCheckBookingAmountAndBookingBalanceRule(dto.getInvoiceAmount(), dto.getDueAmount()));
        //RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(this.closeOperationService, dto.getInvoice().getInvoiceDate().toLocalDate(), dto.getInvoice().getHotel().getId()));
//        command.setInvoice(dto.getInvoice().getId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateLocalDateTime(dto::setHotelCreationDate, command.getHotelCreationDate(), dto.getHotelCreationDate(), update::setUpdate);

        UpdateIfNotNull.updateLocalDateTime(dto::setBookingDate, command.getBookingDate(), dto.getBookingDate(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setHotelBookingNumber, command.getHotelBookingNumber(), dto.getHotelBookingNumber(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setFullName, command.getFullName(), dto.getFullName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setFirstName, command.getFirstName(), dto.getFirstName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setLastName, command.getLastName(), dto.getLastName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setContract, command.getContract(), dto.getContract(), update::setUpdate);

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setRoomNumber, command.getRoomNumber(), dto.getRoomNumber(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCouponNumber, command.getCouponNumber(), dto.getCouponNumber(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setHotelInvoiceNumber, command.getHotelInvoiceNumber(), dto.getHotelInvoiceNumber(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setFolioNumber, command.getFolioNumber(), dto.getFolioNumber(), update::setUpdate);

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);

        this.updateEntity(dto::setRatePlan, command.getRatePlan(), dto.getRatePlan() != null ? dto.getRatePlan().getId() : null, update::setUpdate, this.ratePlanService::findById);
        this.updateEntity(dto::setNightType, command.getNightType(), dto.getNightType() != null ? dto.getNightType().getId() : null, update::setUpdate, this.nightTypeService::findById);
        this.updateEntity(dto::setRoomType, command.getRoomType(), dto.getRoomType() != null ? dto.getRoomType().getId() : null, update::setUpdate, this.roomTypeService::findById);
        this.updateEntity(dto::setRoomCategory, command.getRoomCategory(), dto.getRoomCategory() != null ? dto.getRoomCategory().getId() : null, update::setUpdate, this.roomCategoryService::findById);

        if (update.getUpdate() > 0) {
            this.bookingService.update(dto);
            this.producerUpdateManageInvoiceService.create(this.invoiceService.findById(dto.getInvoice().getId()), null);
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

package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateAdjustmentCommandHandler implements ICommandHandler<UpdateAdjustmentCommand> {

    private final IManageAdjustmentService adjustmentService;
    private final IManageInvoiceTransactionTypeService transactionTypeService;
    private final IManageRoomRateService roomRateService;
    private final IManageBookingService bookingService;
    private final IManageInvoiceService invoiceService;

    private final IInvoiceCloseOperationService closeOperationService;

    public UpdateAdjustmentCommandHandler(IManageAdjustmentService adjustmentService,
            IManageInvoiceTransactionTypeService transactionTypeService, IManageRoomRateService roomRateService,
            IManageBookingService bookingService, IManageInvoiceService invoiceService,
            IInvoiceCloseOperationService closeOperationService) {
        this.adjustmentService = adjustmentService;
        this.transactionTypeService = transactionTypeService;
        this.roomRateService = roomRateService;
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
        this.closeOperationService = closeOperationService;
    }

    @Override
    public void handle(UpdateAdjustmentCommand command) {
        ManageAdjustmentDto dto = this.adjustmentService.findById(command.getId());

        command.setRoomRate(dto.getRoomRate().getId());

        ConsumerUpdate update = new ConsumerUpdate();

       
        UpdateIfNotNull.updateLocalDateTime(dto::setDate, command.getDate(), dto.getDate(), update::setUpdate);

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(),
                dto.getDescription(), update::setUpdate);

        if (command.getTransactionType() != null && !command.getTransactionType().equals("")) {

            this.updateTransactionType(dto::setTransaction, command.getTransactionType(), dto.getTransaction().getId(),
                    update::setUpdate);
        }
        UpdateIfNotNull.updateEntity(dto::setRoomRate, command.getRoomRate(), dto.getRoomRate().getId(),
                update::setUpdate, this.roomRateService::findById);

        ManageRoomRateDto roomRateDto = this.roomRateService.findById(dto.getRoomRate().getId());

        if (command.getAmount() != null) {

            if (command.getAmount() != null) {
                roomRateDto.setInvoiceAmount(roomRateDto.getInvoiceAmount() - dto.getAmount());
                roomRateDto.setInvoiceAmount(roomRateDto.getInvoiceAmount() + command.getAmount());

                this.roomRateService.update(roomRateDto);

            }

            bookingService.calculateInvoiceAmount(this.bookingService.findById(roomRateDto.getBooking().getId()));
            invoiceService.calculateInvoiceAmount(
                    this.invoiceService.findById(roomRateDto.getBooking().getInvoice().getId()));
        }

        UpdateIfNotNull.updateDouble(dto::setAmount, command.getAmount(), dto.getAmount(), update::setUpdate);

        if (update.getUpdate() > 0) {
            if (!dto.getRoomRate().getBooking().getInvoice().getInvoiceType().equals(EInvoiceType.INCOME)) {
                RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(
                        this.closeOperationService,
                        dto.getDate().toLocalDate(),
                        dto.getRoomRate().getBooking().getInvoice().getId()));
            }
            this.adjustmentService.update(dto);
        }
    }

    public void updateTransactionType(Consumer<ManageInvoiceTransactionTypeDto> setter, UUID newValue, UUID oldValue,
            Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageInvoiceTransactionTypeDto transactionTypeDto = this.transactionTypeService.findById(newValue);
            setter.accept(transactionTypeDto);
            update.accept(1);
        }
    }
}

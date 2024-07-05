package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAdjustmentService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTransactionTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateAdjustmentCommandHandler implements ICommandHandler<UpdateAdjustmentCommand> {

    private final IManageAdjustmentService adjustmentService;
    private final IManageInvoiceTransactionTypeService transactionTypeService;
    private final IManageRoomRateService roomRateService;

    public UpdateAdjustmentCommandHandler(IManageAdjustmentService adjustmentService, IManageInvoiceTransactionTypeService transactionTypeService, IManageRoomRateService roomRateService) {
        this.adjustmentService = adjustmentService;
        this.transactionTypeService = transactionTypeService;
        this.roomRateService = roomRateService;
    }

    @Override
    public void handle(UpdateAdjustmentCommand command) {
        ManageAdjustmentDto dto = this.adjustmentService.findById(command.getId());


        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateDouble(dto::setAmount, command.getAmount(), dto.getAmount(), update::setUpdate);
        UpdateIfNotNull.updateLocalDateTime(dto::setDate, command.getDate(), dto.getDate(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);

        if (command.getTransactionType() != null && !command.getTransactionType().equals("")) {

            this.updateTransactionType(dto::setTransaction, command.getTransactionType(), dto.getTransaction().getId(), update::setUpdate);
        }
        UpdateIfNotNull.updateEntity(dto::setRoomRate, command.getRoomRate(), dto.getRoomRate().getId(), update::setUpdate, this.roomRateService::findById);


        if (update.getUpdate() > 0) {
            this.adjustmentService.update(dto);
        }
    }

    public void updateTransactionType(Consumer<ManageInvoiceTransactionTypeDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageInvoiceTransactionTypeDto transactionTypeDto = this.transactionTypeService.findById(newValue);
            setter.accept(transactionTypeDto);
            update.accept(1);
        }
    }
}

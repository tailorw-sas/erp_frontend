package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IIncomeAdjustmentService;
import com.kynsoft.finamer.invoicing.domain.services.IIncomeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTransactionTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManagePaymentTransactionTypeService;

import java.time.LocalDate;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateIncomeAdjustmentCommandHandler implements ICommandHandler<UpdateIncomeAdjustmentCommand> {

    private final IIncomeService incomeService;
    private final IIncomeAdjustmentService incomeAdjustmentService;
    private final IManageInvoiceTransactionTypeService transactionTypeService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;

    public UpdateIncomeAdjustmentCommandHandler(IIncomeService incomeService,
            IIncomeAdjustmentService incomeAdjustmentService,
            IManageInvoiceTransactionTypeService transactionTypeService,  IManagePaymentTransactionTypeService paymentTransactionTypeService) {
        this.incomeService = incomeService;
        this.incomeAdjustmentService = incomeAdjustmentService;
        this.transactionTypeService = transactionTypeService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
    }

    @Override
    public void handle(UpdateIncomeAdjustmentCommand command) {

        // RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id",
        // "Adjustment ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getIncome(), "id", "Income ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getTransactionType(), "transactionType",
                "Manage Invoice Transaction Type ID cannot be null."));

        IncomeAdjustmentDto adjustmentDto = this.incomeAdjustmentService.findById(command.getId());
        IncomeDto incomeDto = this.incomeService.findById(command.getIncome());
        ManageInvoiceTransactionTypeDto transactionTypeDto = command.getTransactionType() != null
                ? this.transactionTypeService.findById(command.getTransactionType())
                : null;

        ManagePaymentTransactionTypeDto paymentTrasnactionTypeDto = command.getTransactionType() != null
                ? this.paymentTransactionTypeService.findById(command.getPaymentTransactionType())
                : null;

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(adjustmentDto::setRemark, command.getRemark(),
                adjustmentDto.getRemark(), update::setUpdate);
        UpdateIfNotNull.updateDouble(adjustmentDto::setAmount, command.getAmount(), adjustmentDto.getAmount(),
                update::setUpdate);
        this.updateStatus(adjustmentDto::setStatus, command.getStatus(), adjustmentDto.getStatus(), update::setUpdate);
        this.updateDate(adjustmentDto::setDate, command.getDate(), adjustmentDto.getDate(), update::setUpdate);

        adjustmentDto.setIncome(incomeDto);
        adjustmentDto.setTransactionType(transactionTypeDto);
        adjustmentDto.setPaymentTransactionType(paymentTrasnactionTypeDto);
        this.incomeAdjustmentService.update(adjustmentDto);

        command.setIncomeResponse(incomeDto);
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

    private void updateDate(Consumer<LocalDate> setter, LocalDate newValue, LocalDate oldValue,
            Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

}

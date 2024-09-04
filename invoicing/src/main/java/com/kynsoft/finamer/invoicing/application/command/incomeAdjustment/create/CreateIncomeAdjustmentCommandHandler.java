package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckAmountNotZeroRule;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckIfIncomeDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CreateIncomeAdjustmentCommandHandler implements ICommandHandler<CreateIncomeAdjustmentCommand> {

        private final IManageAdjustmentService manageAdjustmentService;
        private final IManagePaymentTransactionTypeService transactionTypeService;
        private final IInvoiceCloseOperationService closeOperationService;
        private final IManageBookingService bookingService;

        private final IManageInvoiceService service;

        public CreateIncomeAdjustmentCommandHandler(IManagePaymentTransactionTypeService transactionTypeService,
                        IInvoiceCloseOperationService closeOperationService,
                        IManageBookingService bookingService,
                        IManageAdjustmentService manageAdjustmentService,
                        IManageInvoiceService service) {
                this.transactionTypeService = transactionTypeService;
                this.closeOperationService = closeOperationService;
                this.bookingService = bookingService;
                this.manageAdjustmentService = manageAdjustmentService;
                this.service = service;
        }

        @Override
        public void handle(CreateIncomeAdjustmentCommand command) {

                // RulesChecker.checkRule(new
                // ValidateObjectNotNullRule<>(command.getTransactionType(), "transactionType",
                // "Manage Invoice Transaction Type ID cannot be null."));
                RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getIncome(), "income",
                                "Income ID cannot be null."));

                ManageInvoiceDto incomeDto = this.service.findById(command.getIncome());

                ManageRoomRateDto roomRateDto = new ManageRoomRateDto(
                                UUID.randomUUID(),
                                null,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                0.0,
                                null,
                                null,
                                null,
                                null,
                                null,
                                0.00,
                                "",
                                null,
                                null,
                                null);
                Double invoiceAmount = 0.0;
                List<ManageAdjustmentDto> adjustmentDtos = new ArrayList<>();
                for (NewIncomeAdjustmentRequest adjustment : command.getAdjustments()) {
                        // Puede ser + y -, pero no puede ser 0
                        RulesChecker.checkRule(new CheckAmountNotZeroRule(adjustment.getAmount()));
                        RulesChecker.checkRule(new CheckIfIncomeDateIsBeforeCurrentDateRule(adjustment.getDate()));
                        RulesChecker.checkRule(
                                        new ManageInvoiceInvoiceDateInCloseOperationRule(this.closeOperationService,
                                                        adjustment.getDate(), incomeDto.getHotel().getId()));

                        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = adjustment
                                        .getTransactionType() != null
                                                        ? this.transactionTypeService
                                                                        .findById(adjustment.getTransactionType())
                                                        : null;
                        adjustmentDtos.add(new ManageAdjustmentDto(
                                        UUID.randomUUID(),
                                        0L,
                                        adjustment.getAmount(),
                                        adjustment.getDate().atStartOfDay(),
                                        adjustment.getRemark(),
                                        null,
                                        paymentTransactionTypeDto,
                                        null,
                                        command.getEmployee()));
                        invoiceAmount += adjustment.getAmount();
                }
                ConsumerUpdate updatePayment = new ConsumerUpdate();
                UpdateIfNotNull.updateDouble(incomeDto::setInvoiceAmount, invoiceAmount, updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(incomeDto::setDueAmount, invoiceAmount, updatePayment::setUpdate);
                roomRateDto.setInvoiceAmount(invoiceAmount);
                roomRateDto.setAdjustments(adjustmentDtos);

                List<ManageRoomRateDto> roomRates = new ArrayList<>();
                roomRates.add(roomRateDto);

                ManageBookingDto bookingDto = new ManageBookingDto(
                                UUID.randomUUID(),
                                0L,
                                0L,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                null,
                                null,
                                null,
                                null,
                                invoiceAmount,
                                invoiceAmount,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                0.00,
                                null,
                                incomeDto,
                                null,
                                null,
                                null,
                                null,
                                roomRates,
                                null, null);
                this.bookingService.create(bookingDto);

                // this.manageAdjustmentService.create(adjustmentDtos);

                this.service.update(incomeDto);
                // ManageInvoiceDto updatedIncome = this.service.findById(incomeDto.getId());
                // this.service.calculateInvoiceAmount(updatedIncome);
        }
}

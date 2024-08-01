package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckAmountNotZeroRule;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckIfIncomeDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CreateIncomeAdjustmentCommandHandler implements ICommandHandler<CreateIncomeAdjustmentCommand> {

    private final IManageAdjustmentService manageAdjustmentService;
    private final IManageInvoiceTransactionTypeService transactionTypeService;
    private final IInvoiceCloseOperationService closeOperationService;
    private final IManageBookingService bookingService;

    private final IManageInvoiceService service;

    public CreateIncomeAdjustmentCommandHandler(IManageInvoiceTransactionTypeService transactionTypeService,
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

//        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getTransactionType(), "transactionType", "Manage Invoice Transaction Type ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getIncome(), "income", "Income ID cannot be null."));

        ManageInvoiceDto incomeDto = this.service.findById(command.getIncome());
        ManageInvoiceTransactionTypeDto transactionTypeDto =
                command.getTransactionType() != null
                        ? this.transactionTypeService.findById(command.getTransactionType())
                        : null;
        //InvoiceCloseOperationDto closeOperationDto = this.closeOperationService.findByHotelIds(incomeDto.getHotel().getId());

        //Puede ser + y -, pero no puede ser 0
        RulesChecker.checkRule(new CheckAmountNotZeroRule(command.getAmount()));
        RulesChecker.checkRule(new CheckIfIncomeDateIsBeforeCurrentDateRule(command.getDate()));
        //RulesChecker.checkRule(new CheckIsWithInRangeRule(command.getDate(), closeOperationDto.getBeginDate(), closeOperationDto.getEndDate()));

        ConsumerUpdate updatePayment = new ConsumerUpdate();
        UpdateIfNotNull.updateDouble(incomeDto::setInvoiceAmount, command.getAmount(), updatePayment::setUpdate);

        List<ManageRoomRateDto> roomRates = new ArrayList<>();
        ManageRoomRateDto roomRateDto = new ManageRoomRateDto(
                UUID.randomUUID(), 
                null, 
                LocalDateTime.now(), 
                LocalDateTime.now(),
                command.getAmount(), 
                null, 
                null, 
                null, 
                null, 
                null,
               0.00,
                "", 
                null, 
                null,
                null
        );
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
                command.getAmount(), 
                command.getAmount(), 
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
                null
        );
        this.bookingService.create(bookingDto);
        

        this.manageAdjustmentService.create(new ManageAdjustmentDto(
                command.getId(), 
                0L, 
                command.getAmount(), 
                command.getDate().atStartOfDay(), 
                command.getRemark(), 
                transactionTypeDto, 
                roomRateDto, 
                command.getEmployee()
        ));

        this.service.update(incomeDto);
        ManageInvoiceDto updatedIncome = this.service.findById(incomeDto.getId());
        this.service.calculateInvoiceAmount(updatedIncome);
    }
    
}

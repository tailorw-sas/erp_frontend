package com.kynsoft.finamer.creditcard.application.command.hotelPayment.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.EPaymentTransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.manageBankReconciliation.TransactionCheckInCloseOperationRule;
import com.kynsoft.finamer.creditcard.domain.rules.transaction.TransactionInHotelPaymentRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class CreateHotelPaymentCommandHandler implements ICommandHandler<CreateHotelPaymentCommand> {

    private final IHotelPaymentService hotelPaymentService;

    private final IManageHotelService hotelService;

    private final ITransactionService transactionService;

    private final IManageBankAccountService bankAccountService;

    private final IHotelPaymentAdjustmentService hotelPaymentAdjustmentService;

    private final IManagePaymentTransactionStatusService paymentTransactionStatusService;

    private final ICreditCardCloseOperationService closeOperationService;

    private final IHotelPaymentStatusHistoryService hotelPaymentStatusHistoryService;

    public CreateHotelPaymentCommandHandler(IHotelPaymentService hotelPaymentService, IManageHotelService hotelService, ITransactionService transactionService, IManageBankAccountService bankAccountService, IHotelPaymentAdjustmentService hotelPaymentAdjustmentService, IManagePaymentTransactionStatusService paymentTransactionStatusService, ICreditCardCloseOperationService closeOperationService, IHotelPaymentStatusHistoryService hotelPaymentStatusHistoryService) {
        this.hotelPaymentService = hotelPaymentService;
        this.hotelService = hotelService;
        this.transactionService = transactionService;
        this.bankAccountService = bankAccountService;
        this.hotelPaymentAdjustmentService = hotelPaymentAdjustmentService;
        this.paymentTransactionStatusService = paymentTransactionStatusService;
        this.closeOperationService = closeOperationService;
        this.hotelPaymentStatusHistoryService = hotelPaymentStatusHistoryService;
    }

    @Override
    public void handle(CreateHotelPaymentCommand command) {
        ManageBankAccountDto bankAccountDto = this.bankAccountService.findById(command.getManageBankAccount());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getManageHotel());
        RulesChecker.checkRule(new TransactionCheckInCloseOperationRule(
                this.closeOperationService,
                LocalDateTime.now(), command.getManageHotel()
        ));

        Set<TransactionDto> transactionList = new HashSet<>();
        if (command.getTransactions() != null) {
            for (Long transactionId : command.getTransactions()) {
                TransactionDto transactionDto = this.transactionService.findById(transactionId);
                RulesChecker.checkRule(new TransactionInHotelPaymentRule(transactionDto));
                transactionList.add(transactionDto);
            }
        }

        if (command.getAdjustmentTransactions() != null) {
            this.hotelPaymentAdjustmentService.createAdjustments(command.getAdjustmentTransactions(), transactionList, command.getEmployee());
        }

        double netAmounts = transactionList.stream().map(transactionDto ->
            transactionDto.isAdjustment()
                ? transactionDto.getTransactionSubCategory().getNegative()
                    ? -transactionDto.getNetAmount()
                    : transactionDto.getNetAmount()
                : transactionDto.getNetAmount()
        ).reduce(0.0, Double::sum);

        double amounts = transactionList.stream().map(transactionDto ->
            transactionDto.isAdjustment()
                ? transactionDto.getTransactionSubCategory().getNegative()
                    ? -transactionDto.getAmount()
                    : transactionDto.getAmount()
                : transactionDto.getAmount()
        ).reduce(0.0, Double::sum);
        double commissions = transactionList.stream().map(TransactionDto::getCommission).reduce(0.0, Double::sum);

        ManagePaymentTransactionStatusDto paymentTransactionStatusDto = this.paymentTransactionStatusService.findByEPaymentTransactionStatus(EPaymentTransactionStatus.IN_PROGRESS);
        HotelPaymentDto created = this.hotelPaymentService.create(new HotelPaymentDto(
                command.getId(),
                0L,
                LocalDateTime.now(),
                hotelDto,
                bankAccountDto,
                amounts,
                commissions,
                netAmounts,
                paymentTransactionStatusDto,
                command.getRemark(),
                transactionList,
                null
        ));
        this.hotelPaymentStatusHistoryService.create(created, command.getEmployee());
        command.setHotelPaymentId(created.getHotelPaymentId());
    }

}

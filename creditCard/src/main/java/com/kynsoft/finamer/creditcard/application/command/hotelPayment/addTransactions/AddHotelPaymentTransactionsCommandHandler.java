package com.kynsoft.finamer.creditcard.application.command.hotelPayment.addTransactions;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.rules.transaction.TransactionReconciliationOrPaymentRule;
import com.kynsoft.finamer.creditcard.domain.services.IHotelPaymentAdjustmentService;
import com.kynsoft.finamer.creditcard.domain.services.IHotelPaymentService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AddHotelPaymentTransactionsCommandHandler implements ICommandHandler<AddHotelPaymentTransactionsCommand> {

    private final IHotelPaymentService hotelPaymentService;
    private final IHotelPaymentAdjustmentService hotelPaymentAdjustmentService;
    private final ITransactionService transactionService;

    public AddHotelPaymentTransactionsCommandHandler(IHotelPaymentService hotelPaymentService, IHotelPaymentAdjustmentService hotelPaymentAdjustmentService, ITransactionService transactionService) {
        this.hotelPaymentService = hotelPaymentService;
        this.hotelPaymentAdjustmentService = hotelPaymentAdjustmentService;
        this.transactionService = transactionService;
    }

    @Override
    public void handle(AddHotelPaymentTransactionsCommand command) {
        HotelPaymentDto hotelPaymentDto = this.hotelPaymentService.findById(command.getHotelPaymentId());
        Set<TransactionDto> hotelPaymentTransactions = hotelPaymentDto.getTransactions();
        Set<Long> hotelPaymentTransactionsIds = hotelPaymentDto.getTransactions().stream().map(TransactionDto::getId).collect(Collectors.toSet());

        for (Long transactionId : command.getTransactionIds()) {
            if (!hotelPaymentTransactionsIds.contains(transactionId)) {
                TransactionDto transactionDto = this.transactionService.findById(transactionId);
                RulesChecker.checkRule(new TransactionReconciliationOrPaymentRule(transactionDto));
                hotelPaymentTransactions.add(transactionDto);
                hotelPaymentDto.setNetAmount(hotelPaymentDto.getNetAmount() + transactionDto.getNetAmount());
                hotelPaymentDto.setCommission(hotelPaymentDto.getCommission() + transactionDto.getCommission());
                hotelPaymentDto.setAmount(hotelPaymentDto.getAmount() + transactionDto.getAmount());
            }
        }

        if (command.getAdjustmentRequests() != null && !command.getAdjustmentRequests().isEmpty()) {
            List<Long> adjustmentIds = this.hotelPaymentAdjustmentService.createAdjustments(command.getAdjustmentRequests(), hotelPaymentTransactions);
            command.setAdjustmentIds(adjustmentIds);
        }

        double netAmounts = hotelPaymentTransactions.stream().map(transactionDto ->
            transactionDto.isAdjustment()
                ? transactionDto.getTransactionSubCategory().getNegative()
                    ? -transactionDto.getNetAmount()
                    : transactionDto.getNetAmount()
                : transactionDto.getNetAmount()
        ).reduce(0.0, Double::sum);
        double amounts = hotelPaymentTransactions.stream().map(TransactionDto::getAmount).reduce(0.0, Double::sum);
        double commissions = hotelPaymentTransactions.stream().map(TransactionDto::getCommission).reduce(0.0, Double::sum);

        hotelPaymentDto.setTransactions(hotelPaymentTransactions);
        hotelPaymentDto.setNetAmount(netAmounts);
        hotelPaymentDto.setAmount(amounts);
        hotelPaymentDto.setCommission(commissions);

        command.setNetAmounts(hotelPaymentDto.getNetAmount());
        command.setCommissions(hotelPaymentDto.getCommission());
        command.setAmounts(hotelPaymentDto.getAmount());

        this.hotelPaymentService.update(hotelPaymentDto);
    }
}

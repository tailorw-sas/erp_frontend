package com.kynsoft.finamer.creditcard.application.command.hotelPayment.unbindTransactions;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.services.IHotelPaymentService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

@Component
public class UnbindHotelPaymentTransactionsCommandHandler implements ICommandHandler<UnbindHotelPaymentTransactionsCommand> {

    private final ITransactionService transactionService;
    private final IHotelPaymentService hotelPaymentService;

    public UnbindHotelPaymentTransactionsCommandHandler(ITransactionService transactionService, IHotelPaymentService hotelPaymentService) {
        this.transactionService = transactionService;
        this.hotelPaymentService = hotelPaymentService;
    }

    @Override
    public void handle(UnbindHotelPaymentTransactionsCommand command) {
        HotelPaymentDto hotelPaymentDto = this.hotelPaymentService.findById(command.getHotelPaymentId());
        for (Long id : command.getTransactionsIds()){
            TransactionDto transactionDto = this.transactionService.findById(id);
            if (transactionDto.getHotelPayment() != null && transactionDto.getHotelPayment().getId().equals(command.getHotelPaymentId())){
                if (transactionDto.isAdjustment()){
                    if (hotelPaymentDto.getTransactions().remove(transactionDto)){
                        transactionDto.setHotelPayment(null);
                        this.transactionService.update(transactionDto);
                        this.transactionService.delete(transactionDto);
                        updateHotelPaymentAmounts(hotelPaymentDto);
                    }
                } else {
                    if (hotelPaymentDto.getTransactions().remove(transactionDto)) {
                        transactionDto.setHotelPayment(null);
                        this.transactionService.update(transactionDto);
                        updateHotelPaymentAmounts(hotelPaymentDto);
                    }
                }
            }
        }
        this.hotelPaymentService.update(hotelPaymentDto);
        command.setAmounts(hotelPaymentDto.getAmount());
        command.setNetAmounts(hotelPaymentDto.getNetAmount());
        command.setCommissions(hotelPaymentDto.getCommission());
    }

    private void updateHotelPaymentAmounts(HotelPaymentDto hotelPaymentDto) {
        hotelPaymentDto.setNetAmount(
            hotelPaymentDto.getTransactions().stream().map(dto ->
                dto.isAdjustment()
                    ? dto.getTransactionSubCategory().getNegative()
                        ? -dto.getNetAmount()
                        : dto.getNetAmount()
                    : dto.getNetAmount()
            ).reduce(0.0, Double::sum));

        hotelPaymentDto.setCommission(
                hotelPaymentDto.getTransactions().stream()
                        .map(TransactionDto::getCommission)
                        .reduce(0.0, Double::sum)
        );

        hotelPaymentDto.setAmount(
            hotelPaymentDto.getTransactions().stream().map(dto ->
                dto.isAdjustment()
                    ? dto.getTransactionSubCategory().getNegative()
                        ? -dto.getAmount()
                        : dto.getAmount()
                    : dto.getAmount()
            ).reduce(0.0, Double::sum));
    }
}

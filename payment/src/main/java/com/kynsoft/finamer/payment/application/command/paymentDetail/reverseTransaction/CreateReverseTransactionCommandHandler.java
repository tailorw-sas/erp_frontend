package com.kynsoft.finamer.payment.application.command.paymentDetail.reverseTransaction;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.services.paymentDetail.apply.ReplicateBookingBalanceService;
import com.kynsoft.finamer.payment.application.services.paymentDetail.reverse.ReverseTransactionService;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.helper.ReplicateBookingBalanceHelper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateReverseTransactionCommandHandler implements ICommandHandler<CreateReverseTransactionCommand> {

    private final ReverseTransactionService reverseTransactionService;
    private final ReplicateBookingBalanceService replicateBookingBalanceService;

    public CreateReverseTransactionCommandHandler(ReverseTransactionService reverseTransactionService,
                                                  ReplicateBookingBalanceService replicateBookingBalanceService) {
        this.reverseTransactionService = reverseTransactionService;
        this.replicateBookingBalanceService = replicateBookingBalanceService;
    }

    @Override
    public void handle(CreateReverseTransactionCommand command) {
        PaymentDetailDto updatedPaymentDetail = this.reverseTransactionService.reverseTransaction(command.getPaymentDetail(),
                command.getEmployee());

        PaymentDto payment = this.reverseTransactionService.getPayment();
        ManageBookingDto booking = this.reverseTransactionService.getBooking();

        List<ReplicateBookingBalanceHelper> replicateBookingBalanceHelpers = ReplicateBookingBalanceHelper.from(booking, false);
        this.replicateBookingBalanceService.replicateBooking(replicateBookingBalanceHelpers);
    }
}

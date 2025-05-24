package com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.services.paymentDetail.apply.ReplicateBookingBalanceService;
import com.kynsoft.finamer.payment.application.services.paymentDetail.create.CreatePaymentDetailService;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dto.helper.ReplicateBookingBalanceHelper;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreatePaymentDetailApplyDepositCommandHandler implements ICommandHandler<CreatePaymentDetailApplyDepositCommand> {

    private final CreatePaymentDetailService createPaymentDetailService;
    private final ReplicateBookingBalanceService replicateBookingBalanceService;

    public CreatePaymentDetailApplyDepositCommandHandler(CreatePaymentDetailService createPaymentDetailService,
                                                         ReplicateBookingBalanceService replicateBookingBalanceService) {
        this.createPaymentDetailService = createPaymentDetailService;
        this.replicateBookingBalanceService = replicateBookingBalanceService;
    }

    @Override
    public void handle(CreatePaymentDetailApplyDepositCommand command) {
        PaymentDetailDto paymentDetail = this.createPaymentDetailService.create(command.getId(),
                command.getEmployee(),
                command.getStatus(),
                null,
                command.getTransactionType(),
                command.getAmount(),
                command.getRemark(),
                command.getBooking(),
                command.getApplyPayment(),
                command.getMediator(),
                command.getPaymentDetail());

        PaymentDto payment = createPaymentDetailService.getPayment();
        ManageBookingDto booking = createPaymentDetailService.getBooking();

        if(command.getApplyPayment()){
            List<ReplicateBookingBalanceHelper> replicateBookingBalanceHelpers = ReplicateBookingBalanceHelper.from(booking, false);
            this.replicateBookingBalanceService.replicateBooking(replicateBookingBalanceHelpers);
        }

        command.setPaymentResponse(payment);
    }
}

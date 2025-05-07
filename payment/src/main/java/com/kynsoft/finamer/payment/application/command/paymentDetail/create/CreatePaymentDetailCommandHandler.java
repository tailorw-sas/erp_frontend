package com.kynsoft.finamer.payment.application.command.paymentDetail.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.services.paymentDetail.apply.ReplicateBookingBalanceService;
import com.kynsoft.finamer.payment.application.services.paymentDetail.create.CreatePaymentDetailService;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.helper.ReplicateBookingBalanceHelper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreatePaymentDetailCommandHandler implements ICommandHandler<CreatePaymentDetailCommand> {

    private final CreatePaymentDetailService createPaymentDetailService;
    private final ReplicateBookingBalanceService replicateBookingBalanceService;

    public CreatePaymentDetailCommandHandler(CreatePaymentDetailService createPaymentDetailService,
                                             ReplicateBookingBalanceService replicateBookingBalanceService) {
        this.createPaymentDetailService = createPaymentDetailService;
        this.replicateBookingBalanceService = replicateBookingBalanceService;
    }

    @Override
    public void handle(CreatePaymentDetailCommand command) {
        PaymentDetailDto paymentDetail = createPaymentDetailService.create(
                command.getId(),
                command.getEmployee(),
                command.getStatus(),
                command.getPayment(),
                command.getTransactionType(),
                command.getAmount(),
                command.getRemark(),
                command.getBooking(),
                command.getApplyPayment(),
                command.getMediator(),
                null);

        PaymentDto payment = createPaymentDetailService.getPayment();
        ManageBookingDto booking = createPaymentDetailService.getBooking();

        if(command.getApplyPayment()){
            List<ReplicateBookingBalanceHelper> replicateBookingBalanceHelpers = ReplicateBookingBalanceHelper.from(booking, false);
            this.replicateBookingBalanceService.replicateBooking(replicateBookingBalanceHelpers);
        }

        command.setPaymentResponse(paymentDetail.getPayment());
    }
}

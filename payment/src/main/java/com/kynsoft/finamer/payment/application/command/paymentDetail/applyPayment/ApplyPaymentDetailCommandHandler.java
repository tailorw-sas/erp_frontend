package com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.services.paymentDetail.apply.ApplyPaymentDetailService;
import com.kynsoft.finamer.payment.application.services.paymentDetail.apply.ReplicateBookingBalanceService;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.helper.ReplicateBookingBalanceHelper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplyPaymentDetailCommandHandler implements ICommandHandler<ApplyPaymentDetailCommand> {

    private final ApplyPaymentDetailService applyPaymentDetailService;
    private final ReplicateBookingBalanceService replicateBookingBalanceService;

    public ApplyPaymentDetailCommandHandler(ApplyPaymentDetailService applyPaymentDetailService,
                                            ReplicateBookingBalanceService replicateBookingBalanceService) {
        this.applyPaymentDetailService = applyPaymentDetailService;
        this.replicateBookingBalanceService = replicateBookingBalanceService;
    }

    @Override
    public void handle(ApplyPaymentDetailCommand command) {
        PaymentDto payment = applyPaymentDetailService.applyDetail(
                command.getPaymentDetail(),
                command.getBooking(),
                command.getEmployee());

        PaymentDetailDto paymentDetail = applyPaymentDetailService.getPaymentDetail();
        ManageBookingDto booking = applyPaymentDetailService.getBooking();

        ReplicateBookingBalanceHelper replicateBookingBalanceHelper = ReplicateBookingBalanceHelper.from(payment, paymentDetail, booking, false);
        this.replicateBookingBalanceService.replicateBooking(List.of(replicateBookingBalanceHelper));

        command.setPaymentResponse(payment);
    }
}

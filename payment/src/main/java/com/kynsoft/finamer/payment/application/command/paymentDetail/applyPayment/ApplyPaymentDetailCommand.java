package com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ApplyPaymentDetailCommand implements ICommand {

    private UUID paymentDetail;
    private UUID booking;

    private PaymentDto paymentResponse;

    public ApplyPaymentDetailCommand(UUID paymentDetail, UUID booking) {
        this.paymentDetail = paymentDetail;
        this.booking = booking;
    }

    public static ApplyPaymentDetailCommand fromRequest(ApplyPaymentDetailRequest request) {
        return new ApplyPaymentDetailCommand(
                request.getPaymentDetail(),
                request.getBooking()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new ApplyPaymentDetailMessage(paymentResponse);
    }
}

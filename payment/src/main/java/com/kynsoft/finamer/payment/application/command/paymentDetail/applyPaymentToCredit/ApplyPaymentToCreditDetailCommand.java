package com.kynsoft.finamer.payment.application.command.paymentDetail.applyPaymentToCredit;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ApplyPaymentToCreditDetailCommand implements ICommand {

    private UUID paymentDetail;
    private UUID booking;

    private PaymentDto paymentResponse;

    public ApplyPaymentToCreditDetailCommand(UUID paymentDetail, UUID booking) {
        this.paymentDetail = paymentDetail;
        this.booking = booking;
    }

    @Override
    public ICommandMessage getMessage() {
        return new ApplyPaymentToCreditDetailMessage(paymentResponse);
    }
}

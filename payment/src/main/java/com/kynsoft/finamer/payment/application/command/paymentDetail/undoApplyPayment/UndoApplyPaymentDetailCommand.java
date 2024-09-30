package com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplyPayment;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UndoApplyPaymentDetailCommand implements ICommand {

    private UUID paymentDetail;
    private UUID booking;

    private PaymentDto paymentResponse;

    public UndoApplyPaymentDetailCommand(UUID paymentDetail, UUID booking) {
        this.paymentDetail = paymentDetail;
        this.booking = booking;
    }

    public static UndoApplyPaymentDetailCommand fromRequest(UndoApplyPaymentDetailRequest request) {
        return new UndoApplyPaymentDetailCommand(
                request.getPaymentDetail(),
                request.getBooking()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UndoApplyPaymentDetailMessage(paymentResponse);
    }
}

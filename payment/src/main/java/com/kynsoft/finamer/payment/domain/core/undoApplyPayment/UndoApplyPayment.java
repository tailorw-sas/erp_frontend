package com.kynsoft.finamer.payment.domain.core.undoApplyPayment;

import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;

import java.util.Objects;

public class UndoApplyPayment {

    private final PaymentDetailDto paymentDetail;
    private final ManageBookingDto booking;

    public UndoApplyPayment(PaymentDetailDto paymentDetail,
                            ManageBookingDto booking){
        this.paymentDetail = paymentDetail;
        this.booking = booking;
    }

    public void process(){
        if(Objects.isNull(this.booking) && Objects.isNull(this.paymentDetail)){
            throw new IllegalArgumentException("Booking and Payment detail must not be null");
        }

        this.booking.setAmountBalance(BankerRounding.round(this.booking.getAmountBalance() + paymentDetail.getAmount()));
    }
}

package com.kynsoft.finamer.payment.domain.dto.helper;

import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ReplicateBookingBalanceHelper {

    private ManageBookingDto boooking;
    private UUID paymentId;
    private Long paymentGenId;
    private UUID paymentDetailId;
    private Long paymentDetailGenId;
    private Boolean applyDeposit;

    public static ReplicateBookingBalanceHelper from(PaymentDto payment, PaymentDetailDto detail, ManageBookingDto booking, Boolean applyDeposit) {
        return new ReplicateBookingBalanceHelper(
                booking,
                payment.getId(),
                payment.getPaymentId(),
                detail.getId(),
                detail.getPaymentDetailId(),
                applyDeposit
        );
    }
}

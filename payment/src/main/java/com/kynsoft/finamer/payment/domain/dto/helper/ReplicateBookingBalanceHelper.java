package com.kynsoft.finamer.payment.domain.dto.helper;

import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public static List<ReplicateBookingBalanceHelper> from(PaymentDto payment, List<PaymentDetailDto> detail, Boolean applyDeposit) {
        List<ReplicateBookingBalanceHelper> replicateBookingBalanceHelpers = new ArrayList<>();
        detail.forEach(detailDto -> {
            if(Objects.nonNull(detailDto.getManageBooking())){
                ReplicateBookingBalanceHelper replicateBookingBalanceHelper = new ReplicateBookingBalanceHelper(
                        detailDto.getManageBooking(),
                        payment.getId(),
                        payment.getPaymentId(),
                        detailDto.getId(),
                        detailDto.getPaymentDetailId(),
                        applyDeposit
                );
                replicateBookingBalanceHelpers.add(replicateBookingBalanceHelper);
            }
        });

        return replicateBookingBalanceHelpers;
    }
}

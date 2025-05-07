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
    private Boolean applyDeposit;

    public static List<ReplicateBookingBalanceHelper> from(ManageBookingDto booking, Boolean applyDeposit) {
        return List.of(new ReplicateBookingBalanceHelper(
                booking,
                applyDeposit)
        );
    }

    public static List<ReplicateBookingBalanceHelper> from(List<ManageBookingDto> bookings, Boolean applyDeposit) {
        List<ReplicateBookingBalanceHelper> replicateBookingBalanceHelpers = new ArrayList<>();
        bookings.forEach(booking -> {
            if(Objects.nonNull(booking)){
                ReplicateBookingBalanceHelper replicateBookingBalanceHelper = new ReplicateBookingBalanceHelper(
                        booking,
                        applyDeposit
                );
                replicateBookingBalanceHelpers.add(replicateBookingBalanceHelper);
            }
        });

        return replicateBookingBalanceHelpers;
    }
}

package com.kynsoft.finamer.payment.infrastructure.repository.query.payments;

import com.kynsoft.finamer.payment.infrastructure.identity.Booking;

import java.util.List;

public interface ManageBookingCustomRepository {

    List<Booking> findAllByBookingId(List<Long> ids);

    List<Booking> findAllByCouponIn(List<String> coupons);
}

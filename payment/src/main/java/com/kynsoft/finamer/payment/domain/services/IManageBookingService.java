package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.projection.booking.BookingProjectionControlAmountBalance;
import com.kynsoft.finamer.payment.domain.dto.projection.booking.BookingProjectionSimple;
import com.kynsoft.finamer.payment.infrastructure.identity.Booking;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageBookingService {

    void create(ManageBookingDto dto);

    void update(ManageBookingDto dto);

    ManageBookingDto findById(UUID id);

    ManageBookingDto findByGenId(long id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    boolean exitBookingByGenId(long id);

    void deleteAll();

    BookingProjectionSimple findSimpleDetailByGenId(long id);

    List<ManageBookingDto> findByBookingIdIn(List<Long> ids);

    BookingProjectionControlAmountBalance findSimpleBookingByGenId(long id);

    List<BookingProjectionControlAmountBalance> findAllSimpleBookingByGenId(List<Long> ids);

    List<ManageBookingDto> findAllByBookingIdIn(List<Long> ids);

    List<Booking> findBookingWithEntityGraphByBookingIdIn(List<Long> ids);

    void updateAll(List<Booking> list);

    void updateAllBooking(List<ManageBookingDto> list);

    BookingProjectionControlAmountBalance findByCoupon(String coupon);

    Long countByCoupon(String coupon);

    List<ManageBookingDto> findAllBookingByCoupons(List<String> coupons);

    List<ManageBookingDto> findAllById(List<UUID> ids);
}
